package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Workbook
import org.springframework.data.jpa.repository.JpaRepository

interface WorkbookRepository: JpaRepository<Workbook, Long> {
}