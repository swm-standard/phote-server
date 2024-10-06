package com.swm_standard.phote.entity

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue(value = "SHARED_EXAM")
class SharedExam(
    val starTime: String,
    val endTime: String,
    var capacity: Int,
    member: Member,
    workbook: Workbook,
    sequence: Int,
) : Exam(member, workbook, sequence)
