package com.ybonnel.childpocketmoney.domain.usecase.transaction

import com.google.common.truth.Truth.assertThat
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.core.time.Clock
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.model.TransactionType
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test

class AddCreditUseCaseTest {

    private val now = Instant.fromEpochMilliseconds(1_700_000_000_000L)

    private val clock = object : Clock {
        override fun now(): Instant = now
    }

    private val repository: TransactionRepository = mockk {
        coEvery { insert(any()) } returns 1L
    }

    private val useCase = AddCreditUseCase(repository, clock)

    @Test
    fun `inserts transaction with positive amount`() = runTest {
        useCase(childId = 1L, amount = Money(500L), label = "Tâches")

        val slot = slot<Transaction>()
        coVerify { repository.insert(capture(slot)) }

        with(slot.captured) {
            assertThat(childId).isEqualTo(1L)
            assertThat(amount.cents).isEqualTo(500L)
            assertThat(label).isEqualTo("Tâches")
            assertThat(type).isEqualTo(TransactionType.CREDIT)
            assertThat(occurredAt).isEqualTo(now)
        }
    }

    @Test
    fun `throws on zero amount`() = runTest {
        try {
            useCase(childId = 1L, amount = Money(0L), label = "")
            error("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("positive")
        }
    }

    @Test
    fun `throws on negative amount`() = runTest {
        try {
            useCase(childId = 1L, amount = Money(-100L), label = "")
            error("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("positive")
        }
    }
}

class AddDebitUseCaseTest {

    private val now = Instant.fromEpochMilliseconds(1_700_000_000_000L)

    private val clock = object : Clock {
        override fun now(): Instant = now
    }

    private val repository: TransactionRepository = mockk {
        coEvery { insert(any()) } returns 1L
    }

    private val useCase = AddDebitUseCase(repository, clock)

    @Test
    fun `inserts transaction with negative amount`() = runTest {
        useCase(childId = 2L, amount = Money(300L), label = "Achat")

        val slot = slot<Transaction>()
        coVerify { repository.insert(capture(slot)) }

        with(slot.captured) {
            assertThat(childId).isEqualTo(2L)
            // Debit is stored as negative
            assertThat(amount.cents).isEqualTo(-300L)
            assertThat(label).isEqualTo("Achat")
            assertThat(type).isEqualTo(TransactionType.DEBIT)
        }
    }

    @Test
    fun `throws on zero amount`() = runTest {
        try {
            useCase(childId = 2L, amount = Money(0L), label = "")
            error("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isNotNull()
        }
    }
}
