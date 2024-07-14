package com.swm_standard.phote.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDateTime
import java.util.*

class WorkbookTest {

    @Test
    fun `문제 1개 삭제 시에 quantity가 1만큼 줄어든다`() {
        val workbook: Workbook = createWorkbook()
        val testNum: Int = 10
        workbook.quantity = testNum

        workbook.decreaseQuantity()

        Assertions.assertThat(workbook.quantity).isEqualTo(testNum - 1)
        Assertions.assertThat(workbook.modifiedAt?.second).isEqualTo(LocalDateTime.now().second)
    }

    @Test
    fun `추가한 문제 수만큼 quantity가 증가한다`() {
        val workbook: Workbook = createWorkbook()
        val testNum: Int = 10
        val createdQuestionCnt: Int = 12
        workbook.quantity = testNum

        workbook.increaseQuantity(createdQuestionCnt)

        Assertions.assertThat(workbook.quantity).isEqualTo(testNum + createdQuestionCnt)
        Assertions.assertThat(workbook.modifiedAt?.second).isEqualTo(LocalDateTime.now().second)
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