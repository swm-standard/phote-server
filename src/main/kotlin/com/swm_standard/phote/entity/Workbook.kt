package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.swm_standard.phote.common.exception.BadRequestException
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime
import java.util.UUID

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
) : BaseTimeEntity() {
    @Id
    @Column(name = "workbook_uuid", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID()

    @OneToMany(mappedBy = "workbook", cascade = [(CascadeType.REMOVE)])
    @OrderBy("sequence asc")
    val questionSet: List<QuestionSet>? = null

    @ColumnDefault(value = "0")
    var quantity: Int = 0

    companion object {
        fun createWorkbook(
            title: String,
            description: String?,
            member: Member,
        ) = Workbook(
            title,
            description,
            member,
            matchEmojiByTitle(title),
        )

        fun createSharedWorkbook(
            workbook: Workbook,
            member: Member,
        ) = Workbook(
            "${workbook.title} created by ${workbook.member.name}",
            workbook.description,
            member,
            workbook.emoji,
        )

        private fun matchEmojiByTitle(title: String): String {
            val math: List<String> = listOf("수학", "math", "미적분", "확통", "수1", "수2", "기하", "대수")
            val language: List<String> = listOf("국어", "언매", "화작", "비문학", "문학", "독서", "듣기", "영어", "eng", "토익", "외국")
            val science: List<String> = listOf("과학", "화학", "생물", "생명", "물리", "지구")

            return when {
                math.size != math.filter { !title.contains(it) }.size -> "➗"
                language.size != language.filter { !title.contains(it) }.size -> "💬"
                science.size != science.filter { !title.contains(it) }.size -> "🧪"
                else -> "📚"
            }
        }
    }

    fun decreaseQuantity() {
        if (this.quantity - 1 < 0) {
            throw BadRequestException(fieldName = "quantity", message = "삭제 시 quantity가 음수가 됩니다.")
        }

        this.quantity -= 1
        modifiedAt = LocalDateTime.now()
    }

    fun increaseQuantity(count: Int) {
        this.quantity += count
        modifiedAt = LocalDateTime.now()
    }

    fun compareQuestionQuantity(num: Int) = num == this.quantity

    fun updateWorkbook(
        title: String,
        description: String?,
    ) {
        this.title = title
        this.description = description
        this.emoji = matchEmojiByTitle(title)
    }
}
