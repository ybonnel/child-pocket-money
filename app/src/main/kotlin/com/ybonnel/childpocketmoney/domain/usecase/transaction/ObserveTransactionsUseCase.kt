package com.ybonnel.childpocketmoney.domain.usecase.transaction

import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(childId: Long): Flow<List<Transaction>> =
        repository.observeByChild(childId)
}
