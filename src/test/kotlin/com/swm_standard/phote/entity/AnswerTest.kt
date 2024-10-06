package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.setExp
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AnswerTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `문제가 객관식일 때 정답은 true를 반환한다`() {
        val submittedAnswer = Arbitraries.strings().numeric().sample()
        val correctAnswer = submittedAnswer

        val answer =
            fixtureMonkey
                .giveMeBuilder<Answer>()
                .setExp(Answer::submittedAnswer, submittedAnswer)
                .setExp(
                    Answer::question,
                    fixtureMonkey.giveMeBuilder<Question>().setExp(Question::answer, correctAnswer).sample(),
                ).sample()

        answer.checkMultipleAnswer()

        assertThat(answer.isCorrect).isEqualTo(true)
    }

    @Test
    fun `문제가 객관식일 때 오답은 false를 반환한다`() {
        val submittedAnswer = "5"
        val wrongAnswer = "1"
        val answer =
            fixtureMonkey
                .giveMeBuilder<Answer>()
                .setExp(Answer::submittedAnswer, submittedAnswer)
                .setExp(
                    Answer::question,
                    fixtureMonkey.giveMeBuilder<Question>().setExp(Question::answer, wrongAnswer).sample(),
                ).sample()

        answer.checkMultipleAnswer()

        assertThat(answer.isCorrect).isEqualTo(false)
    }

    @Test
    fun `제출한 답안을 생성한다`() {
        val exam: Exam = fixtureMonkey.giveMeOne()
        val examResult: ExamResult = fixtureMonkey.giveMeBuilder<ExamResult>().setExp(ExamResult::exam, exam).sample()

        val submittedAnswer = Arbitraries.strings().numeric().sample()
        val question =
            fixtureMonkey
                .giveMeBuilder<Question>()
                .setExp(Question::answer, submittedAnswer)
                .sample()
        val sequence = Arbitraries.integers().sample()

        val createAnswer =
            Answer.createAnswer(
                question = question,
                examResult = examResult,
                submittedAnswer = submittedAnswer,
                sequence = sequence,
            )

        assertThat(createAnswer.submittedAnswer).isEqualTo(submittedAnswer)
        assertThat(createAnswer.examResult).isEqualTo(examResult)
        assertThat(createAnswer.sequence).isEqualTo(sequence)
    }
}
