package com.swm_standard.phote.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.SQLDelete
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "UPDATE exam SET deleted_at = NOW() WHERE exam_id = ?")
data class Exam(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,
    @ManyToOne
    @JoinColumn(name = "workbook_id")
    val workbook: Workbook,
    val sequence: Int,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "exam_id", nullable = false, unique = true)
    var id: UUID? = null

    companion object {
        fun createExam(
            member: Member,
            workbook: Workbook,
            sequence: Int,
        ) = Exam(member, workbook, sequence)
    }
}
