package com.example.pocketmoney.core.money

/**
 * Value class representing a monetary amount in cents (Long).
 * Using cents (Long) avoids floating point precision issues.
 */
@JvmInline
value class Money(val cents: Long) {
    operator fun plus(other: Money) = Money(cents + other.cents)
    operator fun minus(other: Money) = Money(cents - other.cents)
    operator fun unaryMinus() = Money(-cents)
    val isPositive: Boolean get() = cents > 0
    val isNegative: Boolean get() = cents < 0
    val isZero: Boolean get() = cents == 0L
    val absoluteValue: Money get() = Money(kotlin.math.abs(cents))

    companion object {
        val Zero = Money(0L)

        /**
         * Convert a decimal amount to Money cents using rounding.
         * Uses Math.round to avoid floating-point truncation (e.g. 19.99 * 100 = 1998.999…).
         */
        fun fromDecimal(amount: Double): Money = Money(Math.round(amount * 100))

        fun fromString(str: String): Money? {
            val cleaned = str.replace(",", ".").trim()
            return cleaned.toDoubleOrNull()?.let { fromDecimal(it) }
        }
    }
}
