package com.ybonnel.childpocketmoney.domain.usecase.transaction

import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.core.time.Clock
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.model.TransactionType
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
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
