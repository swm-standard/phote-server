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
    private val question: Question,
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private val exam: Exam,
    @Column(name = "submitted_answer")
    private val submittedAnswer: String,
    private val isCorrect: Boolean,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    val id: Long = 0

    val sequence: Int = 0
}
