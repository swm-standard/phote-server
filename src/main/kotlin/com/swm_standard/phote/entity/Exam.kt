package com.swm_standard.phote.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class Exam(
    @OneToOne
    @JoinColumn(name = "member_id")
    private val member: Member,
    @ManyToOne
    @JoinColumn(name = "workbook_id")
    private val workbook: Workbook,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    val id: Long = 0

    @CreationTimestamp
    val submittedAt: LocalDateTime = LocalDateTime.now()

    val totalCorrect: Int = 0
}
