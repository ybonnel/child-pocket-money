package com.ybonnel.childpocketmoney.domain.usecase.balance

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveBalanceUseCaseTest {

    private val repository: TransactionRepository = mockk {
        every { observeBalance(any()) } returns flowOf(1500L)
    }

    private val useCase = ObserveBalanceUseCase(repository)

    @Test
    fun `emits Money wrapping cents from repository`() = runTest {
        useCase(childId = 1L).test {
            val money = awaitItem()
            assertThat(money.cents).isEqualTo(1500L)
            awaitComplete()
        }
    }

    @Test
    fun `emits zero when balance is zero`() = runTest {
        every { repository.observeBalance(any()) } returns flowOf(0L)

        useCase(childId = 1L).test {
            val money = awaitItem()
            assertThat(money).isEqualTo(Money.Zero)
            awaitComplete()
        }
    }

    @Test
    fun `emits negative Money for negative balance`() = runTest {
        every { repository.observeBalance(any()) } returns flowOf(-500L)

        useCase(childId = 1L).test {
            val money = awaitItem()
            assertThat(money.cents).isEqualTo(-500L)
            assertThat(money.isNegative).isTrue()
            awaitComplete()
        }
    }
}
