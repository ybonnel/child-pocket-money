package com.example.pocketmoney.domain.model

import com.example.pocketmoney.core.money.Money
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant

/**
 * Domain model for a child profile.
 * [createdAt] is null only when creating a new child; it is populated from the DB on load.
 */
data class Child(
    val id: Long = 0L,
    val name: String,
    val colorArgb: Int,
    val weeklyAllowance: Money = Money.Zero,
    val allowanceDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val allowanceActive: Boolean = true,
    val archived: Boolean = false,
    val createdAt: Instant? = null,
)
