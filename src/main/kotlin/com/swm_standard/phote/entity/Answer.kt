package com.swm_standard.phote.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
data class Answer(
    @OneToOne
    @JoinColumn(name = "question_id")
    private val question: Question,
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private val exam: Exam,
    private val answer: String,
    private val isCorrect: Boolean,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    val id: Long = 0

    val sequence: Int = 0
}
