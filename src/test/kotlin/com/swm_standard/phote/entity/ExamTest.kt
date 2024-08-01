package com.swm_standard.phote.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExamTest {
    private fun createExam(): Exam {

        val member = Member("phote", "phote@test.com", "imageUrl", Provider.KAKAO)
        val workbook = Workbook.createWorkbook(title = "testTitle", description = "", member = member)

        val exam = Exam(
            member = member,
            workbook = workbook
        )

        val answer = Answer(
            question = Question(
                member = member,
                statement = "모든 각이 동일한 삼각형은?",
                image = "http://example.com/image.jpg",
                answer = "정삼각형", category = Category.ESSAY,
                memo = "삼각형 내각의 합은 180도이다."
            ),
            exam = exam,
            submittedAnswer = "정삼각형",
            isCorrect = true
        )

        exam.answers.add(answer)
        exam.answers.add(answer)

        return exam
    }

    @Test
    fun `문제 풀이한 총 문제수를 구한다`() {
        // given
        val exam = createExam()

        // when
        val totalQuantity = exam.calculateTotalQuantity()

        // then
        assertEquals(totalQuantity, 2)
    }
}
