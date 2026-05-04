package com.example.pocketmoney.domain.usecase.transaction

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.core.time.Clock
import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.model.TransactionType
import com.example.pocketmoney.domain.repository.TransactionRepository
import kotlinx.datetime.Instant
import javax.inject.Inject

class AddDebitUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        childId: Long,
        amount: Money,
        label: String,
        occurredAt: Instant = clock.now()
    ): Long {
        require(amount.cents > 0) { "Debit amount must be positive (will be negated)" }
        val transaction = Transaction(
            childId = childId,
            amount = Money(-amount.cents),  // store as negative
            label = label,
            type = TransactionType.DEBIT,
            occurredAt = occurredAt,
            createdAt = clock.now(),
        )
        return repository.insert(transaction)
    }
}
