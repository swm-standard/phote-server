package com.swm_standard.phote.entity

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.size
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class QuestionTest {
    private val fixtureMonkey: FixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .build()

    private fun createQuestion(): Question {
        val objectMapper = ObjectMapper()
        val jsonString = """
        {
            "1": "삼각형",
            "2": "사각형",
            "3": "마름모",
            "4": "평행사변형",
            "5": "원"
        }
        """
        val options: JsonNode = objectMapper.readTree(jsonString)

        return Question(
            member = Member("phote", "phote@test.com", "imageUrl", Provider.KAKAO),
            statement = "꼭짓점이 3개인 도형은?",
            image = "http://example.com/image.jpg",
            options = options,
            answer = "1",
            category = Category.MULTIPLE,
            memo = "삼각형은 꼭짓점이 3개다",
        )
    }

    @Test
    fun `options를 역직렬화 한다`() {
        // given
        val question = createQuestion()

        // when
        val deserializedOptions = question.deserializeOptions()

        // then
        val expectedList = listOf("삼각형", "사각형", "마름모", "평행사변형", "원")
        assertEquals(expectedList, deserializedOptions)
    }

    @Test
    fun `공유받은 문제를 복사해서 저장한다`() {
        val questions = fixtureMonkey.giveMeBuilder<Question>().size(Question::tags, 0).sampleList(5)
        val member: Member = fixtureMonkey.giveMeOne()

        assertNotNull(questions[0])

        println("fixtureMonkey = ${questions[0]}")

        val sharedQuestions = Question.createSharedQuestions(questions, member)

        assertEquals(sharedQuestions.size, questions.size)
        assertEquals(sharedQuestions[0].member, member)
        assertEquals(sharedQuestions[1].category, questions[1].category)
        assertEquals(sharedQuestions[2].answer, questions[2].answer)
    }
}
