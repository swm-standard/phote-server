package com.swm_standard.phote.service

import com.swm_standard.phote.dto.WorkbookCreationResponse
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.WorkbookRepository
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service

@Service
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository
) {

    fun createWorkbook(title: String, description: String?, memberEmail: String): WorkbookCreationResponse {
        val member = memberRepository.findByEmail(memberEmail) ?: throw NotFoundException()
        val workbook = workbookRepository.save(Workbook(title, description, member))

        return WorkbookCreationResponse(workbook.id)
    }
}