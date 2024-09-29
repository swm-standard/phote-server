package com.swm_standard.phote.repository.questionrepository

import com.swm_standard.phote.entity.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuestionRepository :
    JpaRepository<Question, UUID>,
    QuestionCustomRepository
