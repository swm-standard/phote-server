package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Exam
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExamRepository : JpaRepository<Exam, Long>
