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

){

    @Id @Column(name = "workbook_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    @OneToMany(mappedBy = "workbook", cascade = [(CascadeType.REMOVE)])
    @OrderBy("sequence asc")
    val questionSet: List<QuestionSet>? = null

    var emoji: String = "📚"


    @ColumnDefault(value = "0")
    var quantity: Int = 0

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @JsonIgnore
    var deletedAt: LocalDateTime? = null

    @LastModifiedDate
    var modifiedAt: LocalDateTime? = LocalDateTime.now()

    fun decreaseQuantity(){
        this.quantity -= 1
        modifiedAt = LocalDateTime.now()
    }

    fun increaseQuantity(count: Int){
        this.quantity += count
        modifiedAt = LocalDateTime.now()
    }

    fun compareQuestionQuantity(num: Int) = num == this.quantity

    fun matchEmojiByTitle(){
        val math: List<String> = listOf("수학", "math", "미적분", "확통", "수1", "수2", "기하", "대수")
        val language: List<String> = listOf("국어", "언매", "화작", "비문학", "문학", "독서", "듣기", "영어", "eng", "토익", "외국")
        val science: List<String> = listOf("과학", "화학", "생물", "생명", "물리", "지구")

        println("math.size = ${math.size}")
        println("math.filter { it in title }.size = ${math.filter { !title.contains(it) }.size}")
        emoji = when {
            math.size != math.filter { !title.contains(it) }.size -> "➗"
            language.size != language.filter { !title.contains(it) }.size -> "💬"
            science.size != science.filter { !title.contains(it) }.size ->  "🧪"
            else -> "📚"
        }

    }

}
