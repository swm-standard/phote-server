package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Answer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AnswerRepository : JpaRepository<Answer, UUID>
