package com.swm_standard.phote.entity

import jakarta.persistence.*

@Entity
data class QuestionTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_tag_id")
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "question_id")
    val question: Question,

    @ManyToOne
    @JoinColumn(name = "tag_id")
    val tag: Tag,


)
