package com.ybonnel.childpocketmoney.domain.usecase.balance

import com.ybonnel.childpocketmoney.core.time.Clock
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.model.TransactionType
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
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
 * 4. Insert a transaction for each due date (DB unique index prevents duplicates)
 *
 * @param allowanceLabel The user-visible label for automatic allowance transactions.
 *   Passed as a parameter so the domain layer stays locale-agnostic.
 */
class ProcessDueAllowancesUseCase @Inject constructor(
    private val childRepository: ChildRepository,
    private val transactionRepository: TransactionRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(allowanceLabel: String) {
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

            // Start from the day after the last allowance, or from 7 days ago if none.
            // Starting 7 days ago (= today - 6 days back, checking 7 days) avoids
            // flooding when the app is first used.
            var checkDate = if (lastDate == null) {
                today.plus(-6, DateTimeUnit.DAY)
            } else {
                lastDate.plus(1, DateTimeUnit.DAY)
            }

            while (checkDate <= today) {
                if (checkDate.dayOfWeek == child.allowanceDayOfWeek) {
                    val occurredAt = checkDate.atStartOfDayIn(timeZone)
                    transactionRepository.insert(
                        Transaction(
                            childId = child.id,
                            amount = child.weeklyAllowance,
                            label = allowanceLabel,
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
