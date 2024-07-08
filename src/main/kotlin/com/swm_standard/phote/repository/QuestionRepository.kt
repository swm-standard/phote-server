package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Question
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface QuestionRepository: JpaRepository<Question, UUID> {

}