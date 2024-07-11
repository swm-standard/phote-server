package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.CreateWorkbookResponse
import com.swm_standard.phote.dto.DeleteWorkbookResponse
import com.swm_standard.phote.dto.ReadWorkbookDetailResponse
import com.swm_standard.phote.dto.ReadWorkbookListResponse
import com.swm_standard.phote.entity.Member
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.QuestionSetRepository
import com.swm_standard.phote.repository.WorkbookRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository,
    private val questionSetRepository: QuestionSetRepository
) {

    @Transactional
    fun createWorkbook(title: String, description: String?, emoji: String, memberEmail: String): CreateWorkbookResponse {
        val member = memberRepository.findByEmail(memberEmail) ?: throw NotFoundException()
        val workbook = workbookRepository.save(Workbook(title, description, member, emoji))

        return CreateWorkbookResponse(workbook.id)
    }

    @Transactional
    fun deleteWorkbook(id: UUID): DeleteWorkbookResponse {

        workbookRepository.deleteById(id)

        return DeleteWorkbookResponse(id, LocalDateTime.now())
    }

    fun readWorkbookDetail(id: UUID) : ReadWorkbookDetailResponse {
        val workbook = workbookRepository.findById(id).orElseThrow { NotFoundException() }

        val questionSet = questionSetRepository.findAllByWorkbookId(id)

        return ReadWorkbookDetailResponse(
            workbook.id,
            workbook.title,
            workbook.description,
            workbook.emoji,
            workbook.createdAt,
            workbook.modifiedAt,
            questionSet
            )
    }

    fun readWorkbookList(memberId: UUID) : List<ReadWorkbookListResponse> {
        val member: Member = memberRepository.findById(memberId).orElseThrow { InvalidInputException("memberId") }
        val workbooks: List<Workbook> = workbookRepository.findAllByMember(member)

        return workbooks.filter { !it.isDeleted() }.map { workbook ->
            ReadWorkbookListResponse(
                workbook.id,
                workbook.title,
                workbook.description,
                workbook.emoji,
                workbook.quantity,
                workbook.modifiedAt,
            )
        }
    }
}