package com.swm_standard.phote.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Member(
    val name: String,
    val email: String,
    val image: String,
    @Enumerated(EnumType.STRING)
    val provider: Provider,
) : BaseTimeEntity() {
    @Id
    @Column(name = "member_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()
}
