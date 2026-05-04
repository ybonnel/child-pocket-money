package com.example.pocketmoney.data.mapper

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.data.local.entity.ChildEntity
import com.example.pocketmoney.domain.model.Child
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant

fun ChildEntity.toDomain(): Child = Child(
    id = id,
    name = name,
    colorArgb = colorArgb,
    weeklyAllowance = Money(weeklyAllowanceCents),
    allowanceDayOfWeek = DayOfWeek(allowanceDayOfWeek),
    allowanceActive = allowanceActive,
    archived = archived,
    createdAt = Instant.fromEpochMilliseconds(createdAtEpochMs),
)

/**
 * Maps a [Child] domain model to [ChildEntity] for insertion.
 * [createdAtEpochMs] defaults to now only for new records.
 */
fun Child.toEntityForInsert(): ChildEntity = ChildEntity(
    id = id,
    name = name,
    colorArgb = colorArgb,
    weeklyAllowanceCents = weeklyAllowance.cents,
    allowanceDayOfWeek = allowanceDayOfWeek.isoDayNumber,
    allowanceActive = allowanceActive,
    createdAtEpochMs = createdAt?.toEpochMilliseconds() ?: System.currentTimeMillis(),
    archived = archived,
)

/**
 * Maps a [Child] domain model to [ChildEntity] for update.
 * Preserves the original [createdAtEpochMs] from the existing entity.
 */
fun Child.toEntityForUpdate(existingCreatedAtEpochMs: Long): ChildEntity = ChildEntity(
    id = id,
    name = name,
    colorArgb = colorArgb,
    weeklyAllowanceCents = weeklyAllowance.cents,
    allowanceDayOfWeek = allowanceDayOfWeek.isoDayNumber,
    allowanceActive = allowanceActive,
    createdAtEpochMs = existingCreatedAtEpochMs,
    archived = archived,
)
