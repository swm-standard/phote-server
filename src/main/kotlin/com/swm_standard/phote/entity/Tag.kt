package com.swm_standard.phote.entity

import jakarta.persistence.*

@Entity
data class Tag(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    val id: Long,

    val name: String
)
