package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.setExp
import com.navercorp.fixturemonkey.kotlin.sizeExp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExamTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `문제 풀이한 총 문제수를 구한다`() {
        // given
        val exam: Exam =
            fixtureMonkey
                .giveMeBuilder<Exam>()
                .sizeExp(Exam::answers, 2)
                .sample()

        // when
        val totalQuantity = exam.calculateTotalQuantity()

        // then
        assertEquals(totalQuantity, 2)
    }

    @Test
    fun `시험 생성에 성공한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val member: Member = fixtureMonkey.giveMeOne()
        val sequence: Int = 2
        val time = 20

        val exam = Exam.createExam(member, workbook, sequence, time)

        assertThat(exam.workbook).isEqualTo(workbook)
        assertThat(exam.member.name).isEqualTo(member.name)
        assertThat(exam.sequence).isEqualTo(sequence)
    }

    @Test
    fun `totalCorrect가 증가한다`() {
        val count = 3
        val totalCorrect = 2
        val exam =
            fixtureMonkey
                .giveMeBuilder<Exam>()
                .setExp(Exam::totalCorrect, totalCorrect)
                .sample()

        exam.increaseTotalCorrect(count)

        assertThat(exam.totalCorrect).isEqualTo(totalCorrect + count)
    }
}
