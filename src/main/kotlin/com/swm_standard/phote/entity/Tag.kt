package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE tag SET deleted_at = NOW() WHERE tag_id = ?")
@SQLRestriction("deleted_at is NULL")
data class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    val id: Long = 0,

    @Column(unique = true)
    val name: String,

    @JoinColumn(name = "question_id")
    @ManyToOne
    val question: Question,

    @JsonIgnore
    val deletedAt: LocalDateTime? = null
)
