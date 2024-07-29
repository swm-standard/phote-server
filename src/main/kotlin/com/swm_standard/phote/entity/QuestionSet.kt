package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.util.UUID

@Entity
@SQLDelete(sql = "UPDATE question_set SET deleted_at = NOW() WHERE question_set_id = ?")
@SQLRestriction("deleted_at is NULL")
data class QuestionSet(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: Question,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workbook_id")
    @JsonIgnore
    val workbook: Workbook,
    var sequence: Int = 0,
) : BaseTimeEntity() {
    @Id
    @Column(name = "question_set_id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    fun updateSequence(seq: Int) {
        this.sequence = seq
    }

    companion object {
        fun createSequence(
            question: Question,
            workbook: Workbook,
            nextSequence: Int,
        ) = QuestionSet(question, workbook, nextSequence)
    }
}
