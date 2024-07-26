package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@SQLDelete(sql = "UPDATE tag SET deleted_at = NOW() WHERE tag_id = ?")
@SQLRestriction("deleted_at is NULL")
data class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", unique = true)
    @JsonIgnore
    val id: Long = 0L,
    val name: String,
    @JoinColumn(name = "question_id")
    @ManyToOne
    @JsonIgnore
    var question: Question,
) : BaseTimeEntity()
