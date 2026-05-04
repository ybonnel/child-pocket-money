package com.example.pocketmoney.domain.model

import com.example.pocketmoney.core.money.Money
import kotlinx.datetime.DayOfWeek

/**
 * Domain model for a child profile.
 */
data class Child(
    val id: Long = 0L,
    val name: String,
    val colorArgb: Int,
    val weeklyAllowance: Money = Money.Zero,
    val allowanceDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val allowanceActive: Boolean = true,
    val archived: Boolean = false,
)
