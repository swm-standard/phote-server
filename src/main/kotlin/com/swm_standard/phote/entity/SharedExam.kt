package com.swm_standard.phote.entity

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
class SharedExam(
    val starTime: String,
    val endTime: String,
    member: Member,
    workbook: Workbook,
    sequence: Int,
    time: Int,
) : Exam(member, workbook, sequence, time)
