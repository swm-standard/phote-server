package com.swm_standard.phote.repository.workbookrepository

import com.swm_standard.phote.entity.Workbook
import java.util.UUID

interface CustomWorkbookRepository {
    fun findWorkbooksByMember(memberId: UUID): List<Workbook>
}
