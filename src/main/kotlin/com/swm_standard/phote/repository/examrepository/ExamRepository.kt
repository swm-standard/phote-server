package com.swm_standard.phote.repository.examrepository

import com.swm_standard.phote.entity.Exam
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExamRepository :
    JpaRepository<Exam, UUID>,
    ExamCustomRepository {
    fun findAllByWorkbookId(workbookId: UUID): List<Exam>
}
