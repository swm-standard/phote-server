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

    val answer: String?,

    val category: String,

    @OneToMany(mappedBy = "question")
    val questionSet: Set<QuestionSet>?,

    @JoinColumn(name = "tag_id", nullable = true)
    @OneToMany
    val tags: Set<QuestionTag>,

val memo: String?,

    @CreationTimestamp
    val createdAt: LocalDateTime,

    val deletedAt: LocalDateTime?,

    )
