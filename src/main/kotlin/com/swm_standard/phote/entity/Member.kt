package com.swm_standard.phote.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.util.UUID

@Entity
@SQLDelete(sql = "UPDATE question_set SET deleted_at = NOW() WHERE question_set_id = ?")
@SQLRestriction("deleted_at is NULL")
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

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.REMOVE)])
    val questions: List<Question> = emptyList()

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.REMOVE)])
    val workbooks: List<Workbook> = emptyList()

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.REMOVE)])
    val exams: List<Exam> = emptyList()
}
