package com.example.pocketmoney.domain.usecase.transaction

import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(childId: Long): Flow<List<Transaction>> =
        repository.observeByChild(childId)
}
