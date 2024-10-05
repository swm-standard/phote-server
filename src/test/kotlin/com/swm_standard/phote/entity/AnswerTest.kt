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
    fun `문제가 객관식이면 정오답 체크한다`() {
        val submittedAnswer = Arbitraries.strings().numeric().sample()
        val category = Category.MULTIPLE
        val correctAnswer = Arbitraries.strings().numeric().sample()
        val answer =
            fixtureMonkey
                .giveMeBuilder<Answer>()
                .setExp(Answer::submittedAnswer, submittedAnswer)
                .setExp(
                    Answer::question,
                    fixtureMonkey.giveMeBuilder<Question>().setExp(Question::answer, correctAnswer).sample(),
                ).sample()

        answer.checkMultipleAnswer()

        assertThat(submittedAnswer == correctAnswer).isEqualTo(answer.isCorrect)
        assertThat(answer.isCorrect).isFalse()
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
