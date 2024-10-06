package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.setExp
import com.swm_standard.phote.common.exception.BadRequestException
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WorkbookTest {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `문제집을 생성한다`() {
        val member: Member = fixtureMonkey.giveMeOne()
        val testTitle = Arbitraries.strings().sample() + "math"

        val workbook: Workbook = Workbook.createWorkbook(title = testTitle, description = "", member = member)

        assertThat(workbook.title).isEqualTo(testTitle)
        assertThat(workbook.emoji).isEqualTo("➗")
        assertThat(workbook.member.email).isEqualTo(member.email)
        assertThat(workbook.member.provider).isEqualTo(member.provider)
    }

    @Test
    fun `문제집 정보를 수정한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val modifiedTitle = Arbitraries.strings().sample() + "eng"
        val modifiedDescription: String? = Arbitraries.strings().injectNull(0.3).sample()

        workbook.updateWorkbook(modifiedTitle, modifiedDescription)

        assertThat(workbook.title).isEqualTo(modifiedTitle)
        assertThat(workbook.description).isEqualTo(modifiedDescription)
        assertThat(workbook.emoji).isEqualTo("💬")
    }

    @Test
    fun `문제집 내용에 해당하는 키워드가 없으면 📚이모지로 세팅한다`() {
        val workbook: Workbook = fixtureMonkey.giveMeOne()
        val modifiedTitle = "asdsaddsdf 축구 야구"
        val modifiedDescription: String? = Arbitraries.strings().injectNull(0.3).sample()

        workbook.updateWorkbook(modifiedTitle, modifiedDescription)

        assertThat(workbook.title).isEqualTo(modifiedTitle)
        assertThat(workbook.description).isEqualTo(modifiedDescription)
        assertThat(workbook.emoji).isEqualTo("📚")
    }

    @Test
    fun `문제 1개 삭제 시에 quantity가 1만큼 줄어든다`() {
        val testNum: Int = Arbitraries.integers().greaterOrEqual(1).sample()
        val workbook: Workbook =
            fixtureMonkey
                .giveMeBuilder<Workbook>()
                .setExp(Workbook::quantity, testNum)
                .sample()

        workbook.decreaseQuantity()

        assertThat(workbook.quantity).isEqualTo(testNum - 1)
        assertThat(workbook.modifiedAt?.second).isEqualTo(LocalDateTime.now().second)
    }

    @Test
    fun `문제 1개 삭제 시에 quantity가 음수가 될 경우 오류를 반환한다`() {
        val workbook: Workbook =
            fixtureMonkey
                .giveMeBuilder<Workbook>()
                .setExp(Workbook::quantity, 0)
                .sample()

        assertThatThrownBy { workbook.decreaseQuantity() }.isInstanceOf(BadRequestException::class.java)
    }

    @Test
    fun `추가한 문제 수만큼 quantity가 증가한다`() {
        val testNum: Int = Arbitraries.integers().sample()
        val createdQuestionCnt: Int = Arbitraries.integers().greaterOrEqual(1).sample()
        val workbook: Workbook =
            fixtureMonkey
                .giveMeBuilder<Workbook>()
                .setExp(Workbook::quantity, testNum)
                .sample()

        workbook.increaseQuantity(createdQuestionCnt)

        assertThat(workbook.quantity).isEqualTo(testNum + createdQuestionCnt)
        assertThat(workbook.modifiedAt?.second).isEqualTo(LocalDateTime.now().second)
    }

    @Test
    fun `문제집의 문제 개수와 변수로 들어오는 정수를 비교한다`() {
        val quantity = Arbitraries.integers().greaterOrEqual(1).sample()
        val workbook: Workbook =
            fixtureMonkey
                .giveMeBuilder<Workbook>()
                .setExp(Workbook::quantity, quantity)
                .sample()

        val compareQuestionQuantity: Boolean = workbook.compareQuestionQuantity(quantity)

        assertThat(compareQuestionQuantity).isTrue()
    }
}
