package com.swm_standard.phote.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class Answer(
    @ManyToOne
    @JoinColumn(name = "question_id")
    val question: Question,
    @ManyToOne
    @JoinColumn(name = "exam_id")
    val exam: Exam,
    @Column(name = "submitted_answer")
    val submittedAnswer: String,
    val sequence: Int,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    val id: Long = 0

    var isCorrect: Boolean = false

    companion object {
        fun createAnswer(
            question: Question,
            exam: Exam,
            submittedAnswer: String,
            sequence: Int,
        ) = Answer(
            question,
            exam,
            submittedAnswer,
            sequence,
        )
    }

    /**
     * 객관식 문제면 정오답 체크를 하고 true 반환,
     * 주관식 문제면 정오답 여부 판별 없이 false 반환 -> ChatGPT를 이용해야 하기 때문
     **/
    fun isMultipleAndCheckAnswer(): Boolean {
        if (question.category == Category.MULTIPLE) {
            isCorrect = submittedAnswer == question.answer
            return true
        } else {
            return false
        }
    }
}
