package com.swm_standard.phote.entity

import com.swm_standard.phote.common.exception.BadRequestException
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class SharedExam(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    var capacity: Int,
    var examineeCount: Int = 0,
    member: Member,
    workbook: Workbook,
    sequence: Int = 1,
    val title: String,
) : Exam(member, workbook, sequence) {
    companion object {
        fun createSharedExam(
            startTime: LocalDateTime,
            endTime: LocalDateTime,
            member: Member,
            capacity: Int,
            workbook: Workbook,
            title: String,
        ): SharedExam {
            checkTimeValidation(startTime, endTime)
            checkCapacityValidation(capacity)

            return SharedExam(
                startTime = startTime,
                endTime = endTime,
                capacity = capacity,
                member = member,
                workbook = workbook,
                title = title,
            )
        }

        private fun checkTimeValidation(
            start: LocalDateTime,
            end: LocalDateTime,
        ) {
            if (start.isAfter(end)) {
                throw BadRequestException(fieldName = "time", message = "시작 시간이 종료 시간보다 이후입니다.")
            }
        }

        private fun checkCapacityValidation(capacity: Int) {
            if (capacity > 20) {
                throw BadRequestException(fieldName = "capacity", message = "시험 수용 인원은 1명~20명입니다.")
            }
        }
    }

    fun validateSubmissionTime() {
        if (startTime.isAfter(LocalDateTime.now()) || endTime.isBefore(LocalDateTime.now())) {
            throw BadRequestException(fieldName = "submissionTime", message = "제출 시간이 시작 시간 전이거나 종료 시간 이후입니다.")
        }
    }

    fun validateCapacity() {
        if (capacity <= examineeCount) {
            throw BadRequestException(fieldName = "capacity", message = "시험 수용인원을 초과했습니다.")
        }
    }

    fun increaseExamineeCount() {
        validateCapacity()
        examineeCount += 1
    }
}
