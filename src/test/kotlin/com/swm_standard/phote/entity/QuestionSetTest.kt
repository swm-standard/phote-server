package com.swm_standard.phote.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDateTime
import java.util.*

class QuestionSetTest {

    @Test
    fun `문제 순서를 변경한다`() {
        val questionSet = createQuestionSet()
        val sequence = 100

        questionSet.updateSequence(sequence)

        Assertions.assertThat(questionSet.sequence).isEqualTo(sequence)
    }


    fun createQuestionSet(): QuestionSet {
        return QuestionSet(
            question = Question(
                id = UUID.randomUUID(),
                member = Member(
                    id = UUID.randomUUID(),
                    name = "Elena Garza",
                    email = "dennis.mooney@example.com",
                    image = "nec",
                    provider = Provider.APPLE,
                    joinedAt = LocalDateTime.now(),
                    deletedAt = null
                ),
                statement = "Arizona",
                options = null,
                image = null,
                answer = "dicta",
                category = "aliquip",
                questionSet = listOf(),
                memo = null,
                createdAt = LocalDateTime.now(),
                deletedAt = null,
                modifiedAt = null
            ), workbook = createWorkbook()
             ,sequence = 2652
        )
    }

    fun createWorkbook(): Workbook {
        return Workbook(
            title = "deserunt", description = null, member = Member(
                id = UUID.randomUUID(),
                name = "Sandra Downs",
                email = "eddie.henson@example.com",
                image = "disputationi",
                provider = Provider.APPLE,
                joinedAt = LocalDateTime.now(),
                deletedAt = null
            ), emoji = "contentiones"
        )
    }
}