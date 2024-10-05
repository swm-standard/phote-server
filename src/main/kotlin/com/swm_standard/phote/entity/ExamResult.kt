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
data class ExamResult(
    @JoinColumn(name = "member_id")
    @ManyToOne
    val member: Member,
    val time: Int,
    @JoinColumn(name = "exam_id")
    @ManyToOne
    val exam: Exam,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "exam_result_id", nullable = false, unique = true)
    var id: UUID? = null

    var totalCorrect: Int = 0

    @OneToMany(mappedBy = "examResult", cascade = [(CascadeType.REMOVE)])
    val answers: MutableList<Answer> = mutableListOf()

    fun calculateTotalQuantity(): Int = answers.size

    fun increaseTotalCorrect(count: Int) {
        totalCorrect += count
    }

    companion object {
        fun createExamResult(
            member: Member,
            time: Int,
            exam: Exam,
        ) = ExamResult(member, time, exam)
    }
}
