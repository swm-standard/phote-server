package com.swm_standard.phote.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class WorkBook(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workbook_id")
    val id: Long,

    val title: String,

    val description: String?,

    @ManyToOne(cascade = [(CascadeType.REMOVE)])
    @JoinColumn(name = "member_id")
    val member: Member,

    @OneToMany(mappedBy = "workbook", cascade = [(CascadeType.REMOVE)])
    val questionSet: Set<QuestionSet>?,

    @CreationTimestamp
    val createdAt: LocalDateTime,

    val deletedAt: LocalDateTime?,

    )
