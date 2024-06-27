package com.swm_standard.phote.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class Member(

    @Id
    @Column(name = "member_id")
    val id: Long,

//    @OneToMany
//    @JoinColumn(name = "workbookId")
//    val workBook: List<WorkBook>,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    val deletedAt: LocalDateTime?
)


