package com.swm_standard.phote.entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.*

class WorkbookTest {

    @Test
    fun `이미 삭제된 문제에 접근할 수 없다`() {
        val workbook = Workbook(
            title = "cubilia", description = null, member = Member(
                id = UUID.randomUUID(),
                name = "Fern Duran",
                email = "ricky.holloway@example.com",
                image = "consectetuer",
                provider = Provider.APPLE,
                joinedAt = LocalDateTime.now(),
                deletedAt = null
            ), emoji = "😀"
        )
        workbook.deletedAt = LocalDateTime.now()


        assertEquals(workbook.isDeleted(), true)

    }
}