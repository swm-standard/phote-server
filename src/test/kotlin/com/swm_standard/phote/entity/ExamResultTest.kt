package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.setExp
import com.navercorp.fixturemonkey.kotlin.sizeExp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExamResultTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `문제 풀이한 총 문제수를 구한다`() {
        // given
        val exam: ExamResult =
            fixtureMonkey
                .giveMeBuilder<ExamResult>()
                .sizeExp(ExamResult::answers, 2)
                .sample()

        // when
        val totalQuantity = exam.calculateTotalQuantity()

        // then
        assertEquals(totalQuantity, 2)
    }

    @Test
    fun `totalCorrect가 증가한다`() {
        val count = 3
        val totalCorrect = 2
        val exam =
            fixtureMonkey
                .giveMeBuilder<ExamResult>()
                .setExp(ExamResult::totalCorrect, totalCorrect)
                .sample()

        exam.increaseTotalCorrect(count)

        assertThat(exam.totalCorrect).isEqualTo(totalCorrect + count)
    }
}
