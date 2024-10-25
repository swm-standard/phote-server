package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.swm_standard.phote.common.exception.ChatGptErrorException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.ChatGPTRequest
import com.swm_standard.phote.dto.ChatGPTResponse
import com.swm_standard.phote.dto.CreateQuestionRequest
import com.swm_standard.phote.dto.CreateQuestionResponse
import com.swm_standard.phote.dto.DeleteQuestionResponse
import com.swm_standard.phote.dto.ReadQuestionDetailResponse
import com.swm_standard.phote.dto.SearchQuestionsResponse
import com.swm_standard.phote.dto.SearchQuestionsToAddResponse
import com.swm_standard.phote.dto.TransformQuestionResponse
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.Tag
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.TagRepository
import com.swm_standard.phote.repository.questionrepository.QuestionRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import java.time.LocalDateTime
import java.util.UUID

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val memberRepository: MemberRepository,
    private val tagRepository: TagRepository,
    private val template: RestTemplate,
) {
    @Value("\${openai.model}")
    lateinit var model: String

    @Value("\${openai.api.url}")
    lateinit var url: String

    @Value("\${lambda.url}")
    lateinit var lambdaUrl: String

    @Transactional
    fun createQuestion(
        memberId: UUID,
        request: CreateQuestionRequest,
    ): CreateQuestionResponse {
        // 문제 생성 유저 확인
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException("존재하지 않는 member") }

        // 문제 저장
        val question =
            questionRepository.save(
                Question(
                    member = member,
                    statement = request.statement,
                    image = request.image,
                    category = request.category,
                    options = request.options,
                    answer = request.answer,
                    memo = request.memo,
                ),
            )

        // 태그 생성
        request.tags?.map {
            tagRepository.save(Tag(name = it, question = question))
        }

        return CreateQuestionResponse(question.id)
    }

    @Transactional(readOnly = true)
    fun readQuestionDetail(id: UUID): ReadQuestionDetailResponse {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("questionId", "존재하지 않는 UUID") }
        val options = question.options?.let { question.deserializeOptions() }
        return ReadQuestionDetailResponse(question, options)
    }

    @Transactional(readOnly = true)
    fun searchQuestions(
        memberId: UUID,
        tags: List<String>?,
        keywords: List<String>?,
    ): List<SearchQuestionsResponse> =
        questionRepository.searchQuestionsList(memberId, tags, keywords).map { question ->
            val options = question.options?.let { question.deserializeOptions() }
            SearchQuestionsResponse(question, options)
        }

    @Transactional(readOnly = true)
    fun searchQuestionsToAdd(
        memberId: UUID,
        workbookId: UUID,
        tags: List<String>?,
        keywords: List<String>?,
    ): List<SearchQuestionsToAddResponse> =
        questionRepository.searchQuestionsToAddList(memberId, workbookId, tags, keywords)

    @Transactional
    fun deleteQuestion(id: UUID): DeleteQuestionResponse {
        // 존재하지 않는 question id가 아닌지 확인
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("questionId", "존재하지 않는 UUID") }

        // 연결된 workbook의 quantity 감소
        question.questionSet?.map {
            val workbook = it.workbook
            workbook.decreaseQuantity()
        }

        questionRepository.deleteById(id)

        return DeleteQuestionResponse(id, LocalDateTime.now())
    }

    suspend fun transformQuestion(
        imageUrl: String,
        imageCoordinates: List<List<Int>>?,
    ): TransformQuestionResponse {
        lateinit var transformedImageUrlDeferred: Deferred<String?>
        lateinit var chatGPTResponseSplitDeferred: Deferred<List<String>>

        withContext(Dispatchers.IO) {
            transformedImageUrlDeferred = async {
                // 문제 그림 추출
                imageCoordinates?.let { transformImage(imageUrl, it) }
            }

            chatGPTResponseSplitDeferred = async {
                // openAI로 메시지 전송
                val request = ChatGPTRequest(model, imageUrl)
                val chatGPTResponse = template.postForObject(url, request, ChatGPTResponse::class.java)

                // openAI로부터 메시지 수신
                splitChatGPTResponse(chatGPTResponse)
            }
        }
        // 문제 문항과 객관식을 분리해서 dto에 저장
        return TransformQuestionResponse(
            chatGPTResponseSplitDeferred.await()[0],
            chatGPTResponseSplitDeferred.await().drop(1),
            transformedImageUrlDeferred.await()
        )
    }

    suspend fun transformImage(
        imageUrl: String,
        imageCoordinates: List<List<Int>>?,
    ): String? {
        val webClient =
            WebClient
                .builder()
                .baseUrl(lambdaUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .build()

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("url", imageUrl)
        body.add("coor", ObjectMapper().writeValueAsString(imageCoordinates))

        val lambdaResponse: String? =
            webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .awaitBodyOrNull<String>()

        return lambdaResponse?.split("\"")?.get(1)
    }

    fun splitChatGPTResponse(chatGPTResponse: ChatGPTResponse?): List<String> {
        val split: List<String> =
            chatGPTResponse!!
                .choices[0]
                .message.content
                .split("#")

        if (split[0] == "") {
            throw ChatGptErrorException(fieldName = "chatGPT")
        }
        return split
    }
}
