package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
}
