package com.example.pocketmoney.data.mapper

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.data.local.entity.TransactionEntity
import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.model.TransactionType
import kotlinx.datetime.Instant

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    childId = childId,
    amount = Money(amountCents),
    label = label,
    type = TransactionType.valueOf(type),
    occurredAt = Instant.fromEpochMilliseconds(occurredAtEpochMs),
    createdAt = Instant.fromEpochMilliseconds(createdAtEpochMs),
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    childId = childId,
    amountCents = amount.cents,
    label = label,
    type = type.name,
    occurredAtEpochMs = occurredAt.toEpochMilliseconds(),
    createdAtEpochMs = createdAt.toEpochMilliseconds(),
)
