package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@SQLDelete(sql = "UPDATE question SET deleted_at = NOW() WHERE question_uuid = ?")
@SQLRestriction("deleted_at is NULL")
data class Question(

    @Id @Column(name = "question_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    val member: Member,

    @Lob
    val statement: String,

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    val options: JsonNode? = null,

    val image: String?,

    val answer: String,

    @Enumerated(EnumType.STRING)
    val category: Category,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.REMOVE])
    @JsonIgnore
    val questionSet: List<QuestionSet>? = null,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.REMOVE])
    var tags: MutableList<Tag> = mutableListOf(),

    val memo: String?,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonIgnore
    var deletedAt: LocalDateTime? = null,

    @LastModifiedDate
    var modifiedAt: LocalDateTime? = LocalDateTime.now()

)
