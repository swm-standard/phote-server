package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Workbook
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.UUID

class CustomWorkbookRepositoryImpl : CustomWorkbookRepository {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun findWorkbooksByMember(memberId: UUID): List<Workbook> =
        em
            .createQuery(
                "select w from Workbook w" +
                    " join fetch w.member m" +
                    " where m.id = :memberId",
                Workbook::class.java,
            ).setParameter("memberId", memberId)
            .resultList
}
