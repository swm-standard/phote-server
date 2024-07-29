package com.swm_standard.phote.entity

import com.swm_standard.phote.repository.QuestionRepository
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
class QuestionRepositoryTest {
    @Mock
    private lateinit var questionRepository: QuestionRepository

    @Test
    fun `문제를 생성한다`() {
        // given
        val question = Question(
            member = Member("phote", "phote@test.com", "imageUrl", Provider.KAKAO),
            statement = "모든 각이 동일한 삼각형은?",
            image = "http://example.com/image.jpg",
            answer = "정삼각형",
            category = Category.ESSAY,
            memo = "삼각형 내각의 합은 180도이다."
        )

        // when
        Mockito.`when`(questionRepository.save(question)).thenReturn(question)
        val savedQuestion = questionRepository.save(question)

        // then
        assertEquals(savedQuestion.id, question.id)
    }
}
