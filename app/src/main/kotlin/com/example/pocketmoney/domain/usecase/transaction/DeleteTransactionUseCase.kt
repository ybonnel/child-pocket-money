package com.example.pocketmoney.domain.usecase.transaction

import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) = repository.delete(transaction)
}
