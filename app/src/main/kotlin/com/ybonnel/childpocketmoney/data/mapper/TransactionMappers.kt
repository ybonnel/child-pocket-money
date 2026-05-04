package com.ybonnel.childpocketmoney.data.mapper

import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.data.local.entity.TransactionEntity
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.model.TransactionType
import kotlinx.datetime.Instant

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    childId = childId,
    amount = Money(amountCents),
    label = label,
    type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.CREDIT),
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
