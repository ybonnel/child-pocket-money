package com.ybonnel.childpocketmoney.domain.usecase.transaction

import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * Re-inserts a previously deleted transaction (used for undo-delete).
 * The transaction is re-inserted as-is, preserving its original id=0
 * so Room auto-generates a new id (the old one is gone after CASCADE delete).
 */
class ReinsertTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long =
        repository.insert(transaction.copy(id = 0L))
}
