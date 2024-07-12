package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TagRepository: JpaRepository<Tag, Long> {
    fun findAllByQuestionId(questionId: UUID): List<Tag>?
}