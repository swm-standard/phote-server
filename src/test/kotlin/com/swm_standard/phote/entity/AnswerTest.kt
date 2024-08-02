package com.swm_standard.phote.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class AnswerTest {
    @Test
    fun `문제가 객관식이면 정오답 체크를 하고 틀릴 경우 isCorrect가 false가 된다`() {
        val category = Category.MULTIPLE
        val submittedAnswer = "1"
        val correctAnswer = "5"
        val answer = createAnswer(category, submittedAnswer, correctAnswer)

        val checkAnswer = answer.isMultipleAndCheckAnswer()

        Assertions.assertTrue(checkAnswer)
        Assertions.assertFalse(answer.isCorrect)
    }

    @Test
    fun `문제가 주관식이면 false를 반환한다`() {
        val category = Category.ESSAY
        val submittedAnswer = "essay test"
        val correctAnswer = "essay test"
        val answer = createAnswer(category, submittedAnswer, correctAnswer)

        val checkAnswer = answer.isMultipleAndCheckAnswer()

        Assertions.assertFalse(checkAnswer)
    }

    @Test
    fun `답안이 null이면 isCorrect는 false로 체크한다`() {
        val category = Category.MULTIPLE
        val submittedAnswer = null
        val correctAnswer = "5"
        val answer = createAnswer(category, submittedAnswer, correctAnswer)

        val checkAnswer = answer.isMultipleAndCheckAnswer()

        Assertions.assertTrue(checkAnswer)
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
        submittedAnswer = "euismod",
        sequence = 2017,
    )
}
