package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@SQLDelete(sql = "UPDATE workbook SET deleted_at = NOW() WHERE workbook_uuid = ?")
@SQLRestriction("deleted_at is NULL")
data class Workbook(

    var title: String,

    var description: String?,

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    val member: Member,

    var emoji: String,

){

    @Id @Column(name = "workbook_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    @OneToMany(mappedBy = "workbook", cascade = [(CascadeType.REMOVE)])
    @OrderBy("sequence asc")
    val questionSet: List<QuestionSet>? = null

    @ColumnDefault(value = "0")
    var quantity: Int = 0

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @JsonIgnore
    var deletedAt: LocalDateTime? = null

    @LastModifiedDate
    var modifiedAt: LocalDateTime? = LocalDateTime.now()

}
