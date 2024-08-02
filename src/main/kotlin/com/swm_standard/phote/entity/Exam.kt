package com.swm_standard.phote.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
data class Exam(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,
    @ManyToOne
    @JoinColumn(name = "workbook_id")
    val workbook: Workbook,
    val sequence: Int,
    val time: Int,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "exam_id", nullable = false, unique = true)
    var id: UUID? = null

    var totalCorrect: Int = 0

    @OneToMany(mappedBy = "exam", cascade = [(CascadeType.REMOVE)])
    val answers: MutableList<Answer> = mutableListOf()

    fun calculateTotalQuantity(): Int = answers.size

    companion object {
        fun createExam(
            member: Member,
            workbook: Workbook,
            sequence: Int,
            time: Int,
        ) = Exam(member, workbook, sequence, time)
    }

    fun increaseTotalCorrect(count: Int) {
        totalCorrect += count
    }
}
