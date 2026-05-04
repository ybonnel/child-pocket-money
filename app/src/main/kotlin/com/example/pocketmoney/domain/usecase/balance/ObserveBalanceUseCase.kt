package com.example.pocketmoney.domain.usecase.balance

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveBalanceUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(childId: Long): Flow<Money> =
        repository.observeBalance(childId).map { cents -> Money(cents) }
}
