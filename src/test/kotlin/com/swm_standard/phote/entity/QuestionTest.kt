package com.swm_standard.phote.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.setExp
import com.navercorp.fixturemonkey.kotlin.setNotNull
import com.navercorp.fixturemonkey.kotlin.sizeExp
import net.jqwik.api.Arbitraries
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuestionTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `options를 역직렬화 한다`() {
        // given
        val optionList =
            listOf(
                Arbitraries.strings().sample(),
                Arbitraries.strings().sample(),
                Arbitraries.strings().sample(),
                Arbitraries.strings().sample(),
                Arbitraries.strings().sample(),
            )

        val json = """
        {
            "1": "${optionList[0]}",
            "2": "${optionList[1]}",
            "3": "${optionList[2]}",
            "4": "${optionList[3]}",
            "5": "${optionList[4]}"
        }
        """

        val options =
            ObjectMapper().readTree(json)

        val question: Question =
            fixtureMonkey
                .giveMeBuilder<Question>()
                .setExp(Question::category, Category.MULTIPLE)
                .setExp(Question::options, options)
                .sample()

        // when
        val deserializedOptions = question.deserializeOptions()

        // then
        assertEquals(optionList, deserializedOptions)
    }

    @Test
    fun `공유받은 문제를 복사해서 저장한다`() {
        val questions =
            fixtureMonkey
                .giveMeBuilder<Question>()
                .setNotNull(Question::category)
                .setNotNull(Question::statement)
                .setNotNull(Question::answer)
                .sizeExp(Question::tags, 5)
                .sampleList(5)

        val member: Member = fixtureMonkey.giveMeOne()

        val sharedQuestions = Question.createSharedQuestions(questions, member)

        assertEquals(sharedQuestions.size, questions.size)
        assertEquals(sharedQuestions[Arbitraries.integers().between(0, 4).sample()].member.image, member.image)
        assertEquals(sharedQuestions[2].answer, questions[2].answer)
        assertEquals(sharedQuestions[1].tags[0].name, questions[1].tags[0].name)
        assertEquals(sharedQuestions[1].tags[0].id, questions[1].tags[0].id)
    }
}
