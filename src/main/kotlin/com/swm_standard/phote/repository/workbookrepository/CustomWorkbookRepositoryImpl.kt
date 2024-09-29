package com.swm_standard.phote.repository.workbookrepository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.entity.QMember.member
import com.swm_standard.phote.entity.QWorkbook.workbook
import com.swm_standard.phote.entity.Workbook
import java.util.UUID

class CustomWorkbookRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomWorkbookRepository {
    override fun findWorkbooksByMember(memberId: UUID): List<Workbook> =
        jpaQueryFactory
            .selectFrom(workbook)
            .join(workbook.member, member)
            .fetchJoin()
            .where(workbook.member.id.eq(memberId))
            .fetch()
}
