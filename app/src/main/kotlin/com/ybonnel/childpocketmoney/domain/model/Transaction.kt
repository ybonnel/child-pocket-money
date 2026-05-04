package com.ybonnel.childpocketmoney.domain.model

import com.ybonnel.childpocketmoney.core.money.Money
import kotlinx.datetime.Instant

/**
 * Domain model for a transaction.
 * amountCents is signed: positive = credit, negative = debit.
 */
data class Transaction(
    val id: Long = 0L,
    val childId: Long,
    val amount: Money,
    val label: String,
    val type: TransactionType,
    val occurredAt: Instant,
    val createdAt: Instant = occurredAt,
)
