package com.swm_standard.phote.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class Question(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    val id: Long,

    @ManyToOne(cascade = [(CascadeType.REMOVE)])
    @JoinColumn(name = "member_id")
    val member: Member,

    val statement: String,

    val image: String?,

    val category: String,

    val answer: String?,

    @OneToMany(mappedBy = "question", cascade = [(CascadeType.REMOVE)])
    val questionSet: Set<QuestionSet>?,

    @JoinColumn(name = "tag_id", nullable = true)
    @OneToMany
    val tags: Set<Tag>,

    val memo: String?,

    @CreationTimestamp
    val createdAt: LocalDateTime,

    val deletedAt: LocalDateTime?,

    )
