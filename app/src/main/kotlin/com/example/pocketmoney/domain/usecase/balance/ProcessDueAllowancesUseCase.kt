package com.example.pocketmoney.domain.usecase.balance

import com.example.pocketmoney.core.time.Clock
import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.model.TransactionType
import com.example.pocketmoney.domain.repository.ChildRepository
import com.example.pocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * Idempotent use case that creates missing ALLOWANCE transactions.
 * Called at app startup and by WeeklyAllowanceWorker.
 *
 * Algorithm:
 * 1. For each active child with allowance > 0
 * 2. Get the last ALLOWANCE date
 * 3. Find all allowance days between last (exclusive) and now (inclusive)
 * 4. Insert a transaction for each due date
 */
class ProcessDueAllowancesUseCase @Inject constructor(
    private val childRepository: ChildRepository,
    private val transactionRepository: TransactionRepository,
    private val clock: Clock
) {
    suspend operator fun invoke() {
        val timeZone = TimeZone.currentSystemDefault()
        val now = clock.now()
        val today = now.toLocalDateTime(timeZone).date

        val children = childRepository.observeAll().first()

        for (child in children) {
            if (!child.allowanceActive || child.weeklyAllowance.cents <= 0) continue

            val lastAllowanceMs = transactionRepository.lastAllowanceEpochMs(child.id)
            val lastDate = lastAllowanceMs?.let {
                Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date
            }

            // Find all due allowance days
            var checkDate = if (lastDate == null) {
                // No previous allowance: start from 7 days ago to avoid flooding
                today.minus(6, DateTimeUnit.DAY)
            } else {
                lastDate.plus(1, DateTimeUnit.DAY)
            }

            while (checkDate <= today) {
                // Check if this is the allowance day of the week
                if (checkDate.dayOfWeek == child.allowanceDayOfWeek) {
                    val occurredAt = checkDate.atStartOfDayIn(timeZone)
                    transactionRepository.insert(
                        Transaction(
                            childId = child.id,
                            amount = child.weeklyAllowance,
                            label = "Argent de poche hebdomadaire",
                            type = TransactionType.ALLOWANCE,
                            occurredAt = occurredAt,
                            createdAt = now,
                        )
                    )
                }
                checkDate = checkDate.plus(1, DateTimeUnit.DAY)
            }
        }
    }
}

private fun kotlinx.datetime.LocalDate.minus(
    value: Int,
    unit: DateTimeUnit.DateBased
): kotlinx.datetime.LocalDate {
    return plus(-value, unit)
}
