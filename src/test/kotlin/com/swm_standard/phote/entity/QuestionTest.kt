package com.swm_standard.phote.entity

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class QuestionTest {
    private fun createQuestion(): Question {
        val objectMapper = ObjectMapper()
        val jsonString = """
        {
            "1": "삼각형",
            "2": "사각형",
            "3": "마름모",
            "4": "평행사변형",
            "5": "원"
        }
        """
        val options: JsonNode = objectMapper.readTree(jsonString)

        return Question(
            member = Member("phote", "phote@test.com", "imageUrl", Provider.KAKAO),
            statement = "꼭짓점이 3개인 도형은?",
            image = "http://example.com/image.jpg",
            options = options,
            answer = "1",
            category = Category.MULTIPLE,
            memo = "삼각형은 꼭짓점이 3개다",
            tags =
            mutableListOf(
                Tag(
                    id = 1,
                    name = "수학",
                    question =
                    Question(
                        id = UUID.randomUUID(),
                        member =
                        Member(
                            name = "Luella Kline",
                            email = "logan.houston@example.com",
                            image = "et",
                            provider = Provider.APPLE,
                        ),
                        statement = "Louisiana",
                        options = null,
                        image = null,
                        answer = "hac",
                        category = Category.MULTIPLE,
                        questionSet = listOf(),
                        tags = mutableListOf(),
                        memo = null,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `options를 역직렬화 한다`() {
        // given
        val question = createQuestion()

        // when
        val deserializedOptions = question.deserializeOptions()

        // then
        val expectedList = listOf("삼각형", "사각형", "마름모", "평행사변형", "원")
        assertEquals(expectedList, deserializedOptions)
    }

    @Test
    fun `공유받은 문제를 복사해서 저장한다`() {
        val questions = listOf(createQuestion(), createQuestion(), createQuestion())
        val member: Member = createMember()

        val sharedQuestions = Question.createSharedQuestions(questions, member)

        assertEquals(sharedQuestions.size, questions.size)
        assertEquals(sharedQuestions[0].member, member)
        assertEquals(sharedQuestions[2].answer, questions[2].answer)
        assertEquals(sharedQuestions[1].tags[0].name, questions[1].tags[0].name)
        assertEquals(sharedQuestions[1].tags[0].id, questions[1].tags[0].id)
    }

    private fun createMember() =
        Member(
            name = "Wilbur Noel",
            email = "leslie.warner@example.com",
            image = "ocurreret",
            provider = Provider.APPLE,
        )
}
