package com.swm_standard.phote.entity

import jakarta.persistence.*

@Entity
data class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    val id: Long,

    val name: String,

    @OneToMany(mappedBy = "tag")
    val questions: List<QuestionTag>,

)
