package com.ybonnel.childpocketmoney.domain.usecase.balance

import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveBalanceUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(childId: Long): Flow<Money> =
        repository.observeBalance(childId).map { cents -> Money(cents) }
}
