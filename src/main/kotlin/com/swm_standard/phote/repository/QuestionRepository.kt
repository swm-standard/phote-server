package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface QuestionRepository: JpaRepository<Question, UUID> {
    @Query("UPDATE Question q SET q.deletedAt = CURRENT_TIMESTAMP WHERE q.id = :id")
    @Modifying
    override fun deleteById(id: UUID)

    @Query("SELECT q.deletedAt FROM Question q WHERE q.id = :id")
    fun findDeletedAtById(id: UUID): LocalDateTime
}