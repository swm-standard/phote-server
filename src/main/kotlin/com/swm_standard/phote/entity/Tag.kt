package com.swm_standard.phote.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    val id: Long,

    @Column(unique = true)
    val name: String,

    @JoinColumn(name = "question_id")
    @ManyToOne
    val question: Question,

    val deletedAt: LocalDateTime?
)
