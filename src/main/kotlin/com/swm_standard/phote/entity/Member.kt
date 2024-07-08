package com.swm_standard.phote.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Member(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val physicalId: Long,

    @Column(name = "member_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    val name: String,

    val email: String,

    val image: String,

    @Enumerated(EnumType.STRING)
    val provider: Provider,

    @CreationTimestamp
    val joinedAt: LocalDateTime = LocalDateTime.now(),

    val deletedAt: LocalDateTime?
)


