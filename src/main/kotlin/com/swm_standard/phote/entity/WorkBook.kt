package com.swm_standard.phote.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
data class Workbook(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workbook_id")
    val physicalId: Long,

    @Column(name = "workbook_uuid", nullable = false, unique = true)
    val id: UUID,

    val title: String,

    val description: String?,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @OneToMany(mappedBy = "workbook")
    @OrderBy("order asc")
    val questionSet: Set<QuestionSet>?,

    val emoji: String,

    @ColumnDefault(value = "0")
    val quantity: Int,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    val deletedAt: LocalDateTime?,

    @LastModifiedDate
    val modifiedAt: LocalDateTime?

    )
