package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Exam
import org.springframework.data.jpa.repository.JpaRepository

interface ExamRepository : JpaRepository<Exam, Long>
