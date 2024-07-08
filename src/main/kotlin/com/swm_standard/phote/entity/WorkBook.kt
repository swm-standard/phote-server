package com.swm_standard.phote.entity

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
    val member: Member,

    var emoji: String?,

){

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workbook_id")
    val physicalId: Long = 0

    @Column(name = "workbook_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    @OneToMany(mappedBy = "workbook")
    @OrderBy("order asc")
    val questionSet: Set<QuestionSet>? = null

    @ColumnDefault(value = "0")
    var quantity: Int = 0

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    var deletedAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(updatable = false)
    var modifiedAt: LocalDateTime? = null

}
