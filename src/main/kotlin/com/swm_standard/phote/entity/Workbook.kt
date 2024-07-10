package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
data class Workbook(

    var title: String,

    var description: String?,

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    val member: Member,

    var emoji: String?,

){

    @Id @Column(name = "workbook_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    @OneToMany(mappedBy = "workbook")
    @OrderBy("sequence asc")
    val questionSet: List<QuestionSet>? = null

    @ColumnDefault(value = "0")
    var quantity: Int? = null

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @JsonIgnore
    var deletedAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(updatable = false)
    var modifiedAt: LocalDateTime? = null


    fun isDeleted():Boolean = this.deletedAt != null
}
