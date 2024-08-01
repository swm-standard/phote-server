package com.swm_standard.phote.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
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
) : BaseTimeEntity() {
    @Id
    @Column(name = "exam_id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    val totalCorrect: Int = 0

    @OneToMany(mappedBy = "exam", cascade = [(CascadeType.REMOVE)])
    val answers: MutableList<Answer> = mutableListOf()

    val time: Int = 0

    val sequence: Int = 0
}
