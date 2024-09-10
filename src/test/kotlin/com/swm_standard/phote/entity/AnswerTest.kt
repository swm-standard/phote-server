package com.swm_standard.phote.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class AnswerTest {
    @Test
    fun `문제가 객관식이면 정오답 체크한다`() {
        val category = Category.MULTIPLE
        val submittedAnswer = "1"
        val correctAnswer = "5"
        val answer = createAnswer(category, submittedAnswer, correctAnswer)

        answer.checkMultipleAnswer()

        Assertions.assertThat(submittedAnswer == correctAnswer).isEqualTo(answer.isCorrect)
        Assertions.assertThat(answer.isCorrect).isFalse()
    }

    @Test
    fun `제출한 답안을 생성한다`() {
        val exam = createExam()
        val submittedAnswer = "1"
        val question = createQuestion(answer = submittedAnswer)
        val sequence = 2

        val createAnswer =
            Answer.createAnswer(
                question = question,
                exam = exam,
                submittedAnswer = submittedAnswer,
                sequence = sequence,
            )

        Assertions.assertThat(createAnswer.submittedAnswer).isEqualTo(submittedAnswer)
        Assertions.assertThat(createAnswer.exam).isEqualTo(exam)
        Assertions.assertThat(createAnswer.sequence).isEqualTo(sequence)
    }

    fun createWorkbook(): Workbook =
        Workbook(
            title = "hinc",
            description = null,
            member = createMember(),
            emoji = "📚",
        )

    fun createExam() =
        Exam(
            member = createMember(),
            workbook = createWorkbook(),
            sequence = 4282,
            time = 40,
        )

    fun createAnswer(
        category: Category,
        submittedAnswer: String?,
        correctAnswer: String,
    ) = Answer(
        question =
        Question(
            id = UUID.randomUUID(),
            member =
            Member(
                name = "Erik Ashley",
                email = "jay.combs@example.com",
                image = "per",
                provider = Provider.APPLE,
            ),
            statement = "Kentucky",
            options = null,
            image = null,
            answer = correctAnswer,
            category = category,
            questionSet = listOf(),
            memo = null,
        ),
        exam = createExam(),
        submittedAnswer = submittedAnswer,
        sequence = 2017,
    )

    private fun createQuestion(answer: String) =
        Question(
            id = UUID.randomUUID(),
            member =
            Member(
                name = "Charmaine Joseph",
                email = "dorothea.avila@example.com",
                image = "maiorum",
                provider = Provider.APPLE,
            ),
            statement = "Missouri",
            options = null,
            image = null,
            answer = answer,
            category = Category.MULTIPLE,
            questionSet = listOf(),
            tags = mutableListOf(),
            memo = null,
        )

    private fun createMember() =
        Member(
            name = "Wilbur Noel",
            email = "leslie.warner@example.com",
            image = "ocurreret",
            provider = Provider.APPLE,
        )
}
