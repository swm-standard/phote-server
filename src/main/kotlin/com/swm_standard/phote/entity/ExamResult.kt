package com.swm_standard.phote.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.SQLDelete
import java.util.UUID

@Entity
@SQLDelete(sql = "UPDATE exam_result SET deleted_at = NOW() WHERE exam_result_id = ?")
data class ExamResult(
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member,
    val time: Int,
    @JoinColumn(name = "exam_id")
    @ManyToOne
    val exam: Exam,
    val totalQuantity: Int,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "exam_result_id", nullable = false, unique = true)
    var id: UUID? = null

    var totalCorrect: Int = 0

    @OneToMany(mappedBy = "examResult", cascade = [(CascadeType.REMOVE)])
    val answers: MutableList<Answer> = mutableListOf()

    fun increaseTotalCorrect(count: Int) {
        totalCorrect += count
    }

    companion object {
        fun createExamResult(
            member: Member,
            time: Int,
            exam: Exam,
            totalQuantity: Int,
        ) = ExamResult(member, time, exam, totalQuantity)
    }
}
