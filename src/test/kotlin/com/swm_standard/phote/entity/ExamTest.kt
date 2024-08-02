package com.swm_standard.phote.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExamTest {
    private fun createExam(): Exam {
        val member = Member("phote", "phote@test.com", "imageUrl", Provider.KAKAO)
        val workbook = Workbook.createWorkbook(title = "testTitle", description = "", member = member)

        val exam =
            Exam(
                member = member,
                workbook = workbook,
                sequence = 1,
                time = 30,
            )

        val answer =
            Answer(
                question =
                Question(
                    member = member,
                    statement = "모든 각이 동일한 삼각형은?",
                    image = "http://example.com/image.jpg",
                    answer = "정삼각형",
                    category = Category.ESSAY,
                    memo = "삼각형 내각의 합은 180도이다.",
                ),
                exam = exam,
                submittedAnswer = "정삼각형",
                sequence = 1,
            )

        answer.isCorrect = true

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

    @Test
    fun `시험 생성에 성공한다`() {
        val workbook = createWorkbook()
        val member = createMember()
        val sequence: Int = 2
        val time = 20

        val exam = Exam.createExam(member, workbook, sequence, time)

        Assertions.assertThat(exam.workbook).isEqualTo(workbook)
        Assertions.assertThat(exam.member).isEqualTo(member)
        Assertions.assertThat(exam.sequence).isEqualTo(sequence)
    }

    @Test
    fun `totalCorrect가 증가한다`() {
        val exam = createExam()
        val count = 3
        val totalCorrect = exam.totalCorrect

        exam.increaseTotalCorrect(count)

        Assertions.assertThat(exam.totalCorrect).isEqualTo(totalCorrect + count)
    }

    fun createWorkbook(): Workbook =
        Workbook(
            title = "hinc",
            description = null,
            member = createMember(),
            emoji = "📚",
        )

    fun createMember(): Member =
        Member(
            name = "Mayra Payne",
            email = "penelope.mccarty@example.com",
            image = "dicant",
            provider = Provider.APPLE,
        )
}
