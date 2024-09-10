package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@SQLDelete(sql = "UPDATE question SET deleted_at = NOW() WHERE question_uuid = ?")
@SQLRestriction("deleted_at is NULL")
data class Question(
    @Id @Column(name = "question_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),
    @ManyToOne(cascade = [(CascadeType.REMOVE)])
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
) : BaseTimeEntity() {
    fun deserializeOptions(): MutableList<String> {
        val optionsList = mutableListOf<String>()
        options!!.fields().forEach { option ->
            optionsList.add(option.value.asText())
        }

        return optionsList
    }

    companion object {
        fun createSharedQuestions(
            questions: List<Question>,
            member: Member,
        ) = questions.map { question ->
            Question(
                member = member,
                statement = question.statement,
                options = question.options,
                answer = question.answer,
                category = question.category,
                tags = question.tags,
                image = question.image,
                memo = question.memo,
            )
        }
    }
}
