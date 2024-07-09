package com.swm_standard.phote.service

import com.swm_standard.phote.entity.Member
import com.swm_standard.phote.entity.Provider
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.QuestionSetRepository
import com.swm_standard.phote.repository.WorkbookRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.DisplayName
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.UUID

class WorkbookServiceTest (
    @Mock private val workbookRepository: WorkbookRepository,
    @Mock private val questionSetRepository: QuestionSetRepository,
    @InjectMocks private val sut: WorkbookService
){

    @Test
    @DisplayName("문제집 상세 정보 읽기에 성공한다.")
    fun readWorkbookDetail() {
        val workbook = createMockWorkbook()
        val uuid = workbook.id
        Mockito.`when`(workbookRepository.findWorkbookById(uuid)).thenReturn(workbook)
        Mockito.`when`(questionSetRepository.findById(1L)).thenReturn(null)

        val response = sut.readWorkbookDetail(uuid)

        Assertions.assertThat(response.title).isEqualTo(workbook.title)
        Assertions.assertThat(response.description).isEqualTo(workbook.description)
    }

    fun createMockWorkbook(
        title: String = "Sample Workbook",
        description: String? = "Sample Description",
        member: Member = createMockMember(),
        emoji: String? = "😊",
        quantity: Int = 10
    ): Workbook {
        val workbook = Workbook(
            title = title,
            description = description,
            member = member,
            emoji = emoji,
        )
        workbook.quantity = quantity
        return workbook
    }

    private fun createMockMember(): Member {
        return Member(
            email = "test@example.com",
            name = "Test User",
            provider = Provider.GOOGLE,
            id = UUID.randomUUID(),
            joinedAt = LocalDateTime.now(),
            image = "example.com",
            deletedAt = null,
            physicalId = 1L,

        )
    }
}