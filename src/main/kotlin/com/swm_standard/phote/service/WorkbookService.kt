package com.swm_standard.phote.service

import com.swm_standard.phote.dto.CreateWorkbookResponse
import com.swm_standard.phote.dto.DeleteWorkbookResponse
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.WorkbookRepository
import jakarta.transaction.Transactional
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository
) {

    fun createWorkbook(title: String, description: String?, emoji: String?, memberEmail: String): CreateWorkbookResponse {
        val member = memberRepository.findByEmail(memberEmail) ?: throw NotFoundException()
        val workbook = workbookRepository.save(Workbook(title, description, member, emoji))

        return CreateWorkbookResponse(workbook.id)
    }

    @Transactional
    fun deleteWorkbook(id: UUID): DeleteWorkbookResponse {
        var workbook = workbookRepository.findWorkbookById(id) ?: throw NotFoundException()

        workbook.deletedAt = LocalDateTime.now()

        val deletedWorkbook = workbookRepository.save(workbook)

        return DeleteWorkbookResponse(deletedWorkbook.id, deletedWorkbook.deletedAt!!)
    }
}