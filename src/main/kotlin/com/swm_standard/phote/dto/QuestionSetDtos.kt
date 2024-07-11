package com.swm_standard.phote.dto

import com.swm_standard.phote.entity.Question

data class QuestionSetDto(
    val sequence: Int,

    val question: Question
)