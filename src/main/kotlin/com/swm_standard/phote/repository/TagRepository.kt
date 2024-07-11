package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository: JpaRepository<Tag, Long> {
}