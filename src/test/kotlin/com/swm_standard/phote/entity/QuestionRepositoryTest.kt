package com.swm_standard.phote.entity

import com.swm_standard.phote.repository.questionrepository.QuestionRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
class QuestionRepositoryTest {
    @Mock
    private lateinit var questionRepository: QuestionRepository

    private fun createQuestion(): Question =
        Question(
            member = Member("phote", "phote@test.com", "image", Provider.KAKAO),
            statement = "모든 각이 동일한 삼각형은?",
            image = "http://example.com/image.jpg",
            answer = "정삼각형",
            category = Category.ESSAY,
            memo = "삼각형 내각의 합은 180도이다.",
        )

    @Test
    fun `문제를 생성한다`() {
        // given
        val question = createQuestion()

        // when
        Mockito.`when`(questionRepository.save(question)).thenReturn(question)
        val savedQuestion = questionRepository.save(question)

        // then
        assertEquals(savedQuestion.id, question.id)
    }

    @Test
    fun `문제 상세정보를 조회한다`() {
        // given
        val question = createQuestion()

        // when
        Mockito.`when`(questionRepository.findById(question.id)).thenReturn(Optional.of(question))
        val savedQuestion = questionRepository.findById(question.id).orElseThrow()

        // then
        assertEquals(question.member, savedQuestion.member)
        assertEquals("모든 각이 동일한 삼각형은?", savedQuestion.statement)
        assertEquals("http://example.com/image.jpg", savedQuestion.image)
        assertEquals("정삼각형", savedQuestion.answer)
        assertEquals(Category.ESSAY, savedQuestion.category)
        assertEquals("삼각형 내각의 합은 180도이다.", savedQuestion.memo)
    }

    @Test
    fun `문제를 삭제한다`() {
        // given
        val question = createQuestion()

        // when
        questionRepository.deleteById(question.id)

        // then
        assertThrows<NotFoundException> {
            questionRepository.findById(question.id).orElseThrow {
                NotFoundException()
            }
        }
    }
}
