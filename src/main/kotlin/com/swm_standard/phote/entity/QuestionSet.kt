package com.swm_standard.phote.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class QuestionSet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionSet_id")
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "question_id")
    val question: Question,

    @ManyToOne
    @JoinColumn(name = "workbook_id")
    val workbook: WorkBook,

    val createdAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
    )
