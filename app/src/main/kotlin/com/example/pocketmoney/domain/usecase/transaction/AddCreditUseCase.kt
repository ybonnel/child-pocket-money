package com.example.pocketmoney.domain.usecase.transaction

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.core.time.Clock
import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.model.TransactionType
import com.example.pocketmoney.domain.repository.TransactionRepository
import kotlinx.datetime.Instant
import javax.inject.Inject

class AddCreditUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        childId: Long,
        amount: Money,
        label: String,
        occurredAt: Instant = clock.now()
    ): Long {
        require(amount.cents > 0) { "Credit amount must be positive" }
        val transaction = Transaction(
            childId = childId,
            amount = amount,
            label = label,
            type = TransactionType.CREDIT,
            occurredAt = occurredAt,
            createdAt = clock.now(),
        )
        return repository.insert(transaction)
    }
}
