package com.example.pocketmoney.data.mapper

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.data.local.entity.ChildEntity
import com.example.pocketmoney.domain.model.Child
import kotlinx.datetime.DayOfWeek

fun ChildEntity.toDomain(): Child = Child(
    id = id,
    name = name,
    colorArgb = colorArgb,
    weeklyAllowance = Money(weeklyAllowanceCents),
    allowanceDayOfWeek = DayOfWeek(allowanceDayOfWeek),
    allowanceActive = allowanceActive,
    archived = archived,
)

fun Child.toEntity(createdAtEpochMs: Long = System.currentTimeMillis()): ChildEntity = ChildEntity(
    id = id,
    name = name,
    colorArgb = colorArgb,
    weeklyAllowanceCents = weeklyAllowance.cents,
    allowanceDayOfWeek = allowanceDayOfWeek.isoDayNumber,
    allowanceActive = allowanceActive,
    createdAtEpochMs = createdAtEpochMs,
    archived = archived,
)
