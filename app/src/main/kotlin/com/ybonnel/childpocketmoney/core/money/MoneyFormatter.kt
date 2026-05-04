package com.ybonnel.childpocketmoney.core.money

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Formats Money values into localized currency strings.
 */
object MoneyFormatter {

    fun format(money: Money, currencyCode: String): String {
        return try {
            val currency = Currency.getInstance(currencyCode)
            val format = NumberFormat.getCurrencyInstance()
            format.currency = currency
            format.format(money.cents / 100.0)
        } catch (e: Exception) {
            "${money.cents / 100.0} $currencyCode"
        }
    }

    fun formatSigned(money: Money, currencyCode: String): String {
        val formatted = format(money.absoluteValue, currencyCode)
        return when {
            money.isPositive -> "+$formatted"
            money.isNegative -> "-$formatted"
            else -> formatted
        }
    }

    /**
     * Returns amount as decimal string for editing (e.g. "5.00")
     */
    fun toDecimalString(money: Money): String {
        val absAmount = money.absoluteValue.cents
        val euros = absAmount / 100
        val cents = absAmount % 100
        return "$euros.${cents.toString().padStart(2, '0')}"
    }
}
