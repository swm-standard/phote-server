package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.setExp
import com.swm_standard.phote.common.exception.BadRequestException
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.Month

class ExamTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `시험 생성에 성공한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val member: Member = fixtureMonkey.giveMeOne()
        val sequence = 2

        val exam = Exam.createExam(member, workbook, sequence)

        assertThat(exam.workbook).isEqualTo(workbook)
        assertThat(exam.member.name).isEqualTo(member.name)
        assertThat(exam.sequence).isEqualTo(sequence)
    }

    @Test
    fun `공유용 시험 생성에 성공한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val member: Member = fixtureMonkey.giveMeOne()
        val capacity: Int =
            Arbitraries
                .integers()
                .greaterOrEqual(1)
                .lessOrEqual(20)
                .sample()
        val title: String = Arbitraries.strings().sample()
        val startTime: LocalDateTime = LocalDateTime.of(2024, Month.OCTOBER, 6, 10, 0)
        val endTime: LocalDateTime = LocalDateTime.of(2024, Month.OCTOBER, 6, 11, 0)

        val sharedExam = SharedExam.createSharedExam(startTime, endTime, member, capacity, workbook, title)

        assertThat(sharedExam.workbook).isEqualTo(workbook)
        assertThat(sharedExam.startTime).isBefore(sharedExam.endTime)
        assertThat(sharedExam.capacity).isEqualTo(capacity)
    }

    @Test
    fun `공유용 시험 생성 시 시작 시간이 종료 시간 이후이면 실패한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val member: Member = fixtureMonkey.giveMeOne()
        val capacity: Int = Arbitraries.integers().sample()
        val title: String = Arbitraries.strings().sample()
        val startTime: LocalDateTime = LocalDateTime.of(2024, Month.OCTOBER, 6, 10, 0)
        val endTime: LocalDateTime = startTime.minusMinutes(10L)

        assertThrows<BadRequestException> {
            SharedExam.createSharedExam(
                startTime,
                endTime,
                member,
                capacity,
                workbook,
                title,
            )
        }
    }

    @Test
    fun `공유용 시험 생성 시 수용인원이 1~20명 밖이면 실패한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val member: Member = fixtureMonkey.giveMeOne()
        val capacity: Int = Arbitraries.integers().greaterOrEqual(21).sample()
        val title: String = Arbitraries.strings().sample()
        val startTime: LocalDateTime = LocalDateTime.of(2024, Month.OCTOBER, 6, 10, 0)
        val endTime: LocalDateTime = LocalDateTime.of(2024, Month.OCTOBER, 6, 10, 30)

        assertThrows<BadRequestException> {
            SharedExam.createSharedExam(
                startTime,
                endTime,
                member,
                capacity,
                workbook,
                title,
            )
        }
    }

    @Test
    fun `공유용 시험 풀이 제출 시간이 시작 시간 전이면 실패한다`() {
        val sharedExam =
            fixtureMonkey
                .giveMeBuilder(SharedExam::class.java)
                .setExp(SharedExam::startTime, LocalDateTime.now().plusDays(1))
                .build()
                .sample()

        assertThrows<BadRequestException> {
            sharedExam.validateSubmissionTime()
        }
    }

    @Test
    fun `공유용 시험의 시험 제출 인원 수를 증가시킨다`() {
        val examineeCount = Arbitraries.integers().sample()
        val sharedExam =
            fixtureMonkey
                .giveMeBuilder(SharedExam::class.java)
                .setExp(SharedExam::startTime, LocalDateTime.now().plusDays(1))
                .setExp(SharedExam::examineeCount, examineeCount)
                .build()
                .sample()

        sharedExam.increaseExamineeCount()

        assertThat(sharedExam.examineeCount).isEqualTo(examineeCount + 1)
    }

    @Test
    fun `공유용 시험의 수용 인원을 초과하면 인원 수 증가에 실패한다`() {
        val examineeCount = Arbitraries.integers().sample()
        val sharedExam =
            fixtureMonkey
                .giveMeBuilder(SharedExam::class.java)
                .setExp(SharedExam::startTime, LocalDateTime.now().plusDays(1))
                .setExp(SharedExam::examineeCount, examineeCount)
                .setExp(SharedExam::capacity, examineeCount)
                .build()
                .sample()

        assertThrows<BadRequestException> {
            sharedExam.increaseExamineeCount()
        }
        assertThat(sharedExam.examineeCount).isEqualTo(examineeCount)
    }
}
