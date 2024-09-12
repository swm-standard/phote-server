package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import net.jqwik.api.Arbitraries
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuestionSetTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `questionSet의 순서를 변경하는데 성공한다`() {
        val questionSet: QuestionSet = fixtureMonkey.giveMeOne()
        val newSequence = Arbitraries.integers().sample()

        questionSet.updateSequence(newSequence)

        assertEquals(newSequence, questionSet.sequence)
    }

    @Test
    fun `questionSet을 생성하는데 성공한다`() {
        val question: Question = fixtureMonkey.giveMeOne()
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val newSequence = Arbitraries.integers().sample()

        val questionSet =
            QuestionSet.createQuestionSet(
                question = question,
                workbook = workbook,
                nextSequence = newSequence,
            )

        assertEquals(newSequence, questionSet.sequence)
        assertEquals(workbook, questionSet.workbook)
        assertEquals(question, questionSet.question)
    }
}
