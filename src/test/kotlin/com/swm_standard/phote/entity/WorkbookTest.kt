package com.swm_standard.phote.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WorkbookTest {
    @Test
    fun `문제집을 생성한다`() {
        val member: Member = createMember()
        val testTitle = "테스트 제목 수학"

        val workbook: Workbook = Workbook.createWorkbook(title = testTitle, description = "", member = member)

        Assertions.assertThat(workbook.title).isEqualTo(testTitle)
        Assertions.assertThat(workbook.emoji).isEqualTo("➗")
        Assertions.assertThat(workbook.member.email).isEqualTo(member.email)
    }

    @Test
    fun `문제집 정보를 수정한다`() {
        val workbook: Workbook = createWorkbook()
        val modifiedTitle = "수정 제목 english"
        val modifiedDescription = "수정 설명"

        workbook.updateWorkbook(modifiedTitle, modifiedDescription)

        Assertions.assertThat(workbook.title).isEqualTo(modifiedTitle)
        Assertions.assertThat(workbook.description).isEqualTo(modifiedDescription)
        Assertions.assertThat(workbook.emoji).isEqualTo("💬")
    }

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

    @Test
    fun `문제집의 문제 개수와 변수로 들어오는 정수를 비교한다`() {
        val workbook: Workbook = createWorkbook()
        val quantity = 2
        workbook.quantity = quantity

        val compareQuestionQuantity: Boolean = workbook.compareQuestionQuantity(quantity)

        Assertions.assertThat(compareQuestionQuantity).isTrue()
    }
}
