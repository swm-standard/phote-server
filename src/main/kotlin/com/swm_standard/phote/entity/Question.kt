package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*


@Entity
data class Question(

    @Id @Column(name = "question_uuid", nullable = false, unique = true)
    val id: UUID,

    @ManyToOne(cascade = [(CascadeType.REMOVE)])
    @JoinColumn(name = "member_id")
    @JsonIgnore
    val member: Member,

    @Lob
    val statement: String,

    val options: String,

    val image: String?,

    val answer: String?,

    val category: String,

    @OneToMany(mappedBy = "question")
    val questionSet: Set<QuestionSet>?,

    @JoinColumn(name = "tag_id", nullable = true)
    @OneToMany
    val tags: List<Tag>,

    val memo: String?,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonIgnore
    val deletedAt: LocalDateTime?,

    @LastModifiedDate
    val modifiedAt: LocalDateTime?,

    )
