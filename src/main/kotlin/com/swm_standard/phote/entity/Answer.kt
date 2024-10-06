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
    val question: Question?,
    @ManyToOne
    @JoinColumn(name = "exam_result_id")
    val examResult: ExamResult,
    @Column(name = "submitted_answer")
    val submittedAnswer: String?,
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
            examResult: ExamResult,
            submittedAnswer: String?,
            sequence: Int,
        ) = Answer(
            question,
            examResult,
            submittedAnswer,
            sequence,
        )
    }

    fun checkMultipleAnswer() {
        isCorrect = submittedAnswer == question?.answer
    }
}
