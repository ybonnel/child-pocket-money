package com.ybonnel.childpocketmoney.core.money

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MoneyTest {

    // ── fromDecimal ────────────────────────────────────────────────────────────

    @Test
    fun `fromDecimal converts whole euro correctly`() {
        assertThat(Money.fromDecimal(5.0).cents).isEqualTo(500L)
    }

    @Test
    fun `fromDecimal converts 19_99 without losing a cent`() {
        // 19.99 * 100 = 1998.9999… in IEEE-754; toLong() would give 1998, not 1999.
        assertThat(Money.fromDecimal(19.99).cents).isEqualTo(1999L)
    }

    @Test
    fun `fromDecimal converts 9_99 correctly`() {
        assertThat(Money.fromDecimal(9.99).cents).isEqualTo(999L)
    }

    @Test
    fun `fromDecimal converts 0_01 correctly`() {
        assertThat(Money.fromDecimal(0.01).cents).isEqualTo(1L)
    }

    @Test
    fun `fromDecimal converts zero`() {
        assertThat(Money.fromDecimal(0.0).cents).isEqualTo(0L)
    }

    @Test
    fun `fromDecimal rounds half-up`() {
        // 0.005 should round to 1 cent
        assertThat(Money.fromDecimal(0.005).cents).isEqualTo(1L)
    }

    // ── fromString ─────────────────────────────────────────────────────────────

    @Test
    fun `fromString parses decimal with dot`() {
        assertThat(Money.fromString("5.50")?.cents).isEqualTo(550L)
    }

    @Test
    fun `fromString parses decimal with comma`() {
        assertThat(Money.fromString("5,50")?.cents).isEqualTo(550L)
    }

    @Test
    fun `fromString returns null for invalid input`() {
        assertThat(Money.fromString("abc")).isNull()
    }

    @Test
    fun `fromString returns null for empty string`() {
        assertThat(Money.fromString("")).isNull()
    }

    @Test
    fun `fromString handles whitespace`() {
        assertThat(Money.fromString("  10.00  ")?.cents).isEqualTo(1000L)
    }

    // ── arithmetic ─────────────────────────────────────────────────────────────

    @Test
    fun `plus adds two Money values`() {
        val result = Money(300L) + Money(200L)
        assertThat(result.cents).isEqualTo(500L)
    }

    @Test
    fun `minus subtracts Money values`() {
        val result = Money(500L) - Money(200L)
        assertThat(result.cents).isEqualTo(300L)
    }

    @Test
    fun `minus can produce negative result`() {
        val result = Money(100L) - Money(200L)
        assertThat(result.cents).isEqualTo(-100L)
    }

    @Test
    fun `unaryMinus negates Money`() {
        assertThat((-Money(500L)).cents).isEqualTo(-500L)
    }

    // ── predicates ─────────────────────────────────────────────────────────────

    @Test
    fun `isPositive returns true for positive cents`() {
        assertThat(Money(1L).isPositive).isTrue()
    }

    @Test
    fun `isPositive returns false for zero`() {
        assertThat(Money(0L).isPositive).isFalse()
    }

    @Test
    fun `isNegative returns true for negative cents`() {
        assertThat(Money(-1L).isNegative).isTrue()
    }

    @Test
    fun `isZero returns true for zero`() {
        assertThat(Money.Zero.isZero).isTrue()
    }

    @Test
    fun `absoluteValue returns positive for negative money`() {
        assertThat(Money(-500L).absoluteValue.cents).isEqualTo(500L)
    }
}
