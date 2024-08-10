package com.swm_standard.phote.entity

import org.junit.jupiter.api.Assertions
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

        Assertions.assertEquals(submittedAnswer == correctAnswer, answer.isCorrect)
        Assertions.assertFalse(answer.isCorrect)
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
}
