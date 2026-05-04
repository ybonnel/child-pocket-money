package com.ybonnel.childpocketmoney.domain.usecase.balance

import com.google.common.truth.Truth.assertThat
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.core.time.Clock
import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.model.TransactionType
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock as KotlinClock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.junit.Test

class ProcessDueAllowancesUseCaseTest {

    // ── Test doubles ──────────────────────────────────────────────────────────

    private class FakeChildRepository(private val children: List<Child>) : ChildRepository {
        override fun observeAll(): Flow<List<Child>> = flowOf(children)
        override fun observeById(id: Long): Flow<Child?> =
            flowOf(children.find { it.id == id })
        override suspend fun insert(child: Child): Long = child.id
        override suspend fun update(child: Child) {}
        override suspend fun archive(id: Long) {}
    }

    private class FakeTransactionRepository : TransactionRepository {
        val inserted = mutableListOf<Transaction>()
        var lastAllowanceMs: Long? = null

        override fun observeByChild(childId: Long): Flow<List<Transaction>> = flowOf(inserted)
        override fun observeBalance(childId: Long): Flow<Long> = flowOf(0L)
        override suspend fun lastAllowanceEpochMs(childId: Long): Long? = lastAllowanceMs
        override suspend fun insert(transaction: Transaction): Long {
            inserted.add(transaction)
            return inserted.size.toLong()
        }
        override suspend fun delete(transaction: Transaction) {
            inserted.remove(transaction)
        }
    }

    private class FakeClock(private val instant: Instant) : Clock {
        override fun now(): Instant = instant
    }

    // Helper to build a fixed instant at midnight of a given date in system TZ
    private fun instantOf(year: Int, month: Int, day: Int): Instant =
        LocalDate(year, month, day).atStartOfDayIn(TimeZone.currentSystemDefault())

    private fun makeChild(
        allowanceDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        allowanceCents: Long = 500L,
        active: Boolean = true,
    ) = Child(
        id = 1L,
        name = "Test",
        colorArgb = 0,
        weeklyAllowance = Money(allowanceCents),
        allowanceDayOfWeek = allowanceDayOfWeek,
        allowanceActive = active,
    )

    // ── Tests ──────────────────────────────────────────────────────────────────

    @Test
    fun `inserts allowance on first run when today is allowance day`() = runTest {
        // 2024-01-08 is a Monday
        val now = instantOf(2024, 1, 8)
        val child = makeChild(allowanceDayOfWeek = DayOfWeek.MONDAY)
        val txRepo = FakeTransactionRepository()
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        assertThat(txRepo.inserted).hasSize(1)
        assertThat(txRepo.inserted[0].type).isEqualTo(TransactionType.ALLOWANCE)
        assertThat(txRepo.inserted[0].amount.cents).isEqualTo(500L)
    }

    @Test
    fun `does not insert when today is not allowance day`() = runTest {
        // 2024-01-09 is a Tuesday
        val now = instantOf(2024, 1, 9)
        val child = makeChild(allowanceDayOfWeek = DayOfWeek.MONDAY)
        val txRepo = FakeTransactionRepository()
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        assertThat(txRepo.inserted).isEmpty()
    }

    @Test
    fun `idempotent - does not insert second allowance on same day`() = runTest {
        // 2024-01-08 is a Monday — allowance already paid on that day
        val allowanceDay = instantOf(2024, 1, 8)
        val now = allowanceDay  // same instant
        val child = makeChild(allowanceDayOfWeek = DayOfWeek.MONDAY)
        val txRepo = FakeTransactionRepository().also {
            it.lastAllowanceMs = allowanceDay.toEpochMilliseconds()
        }
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        // Last allowance was today, next due is in 7 days → nothing inserted
        assertThat(txRepo.inserted).isEmpty()
    }

    @Test
    fun `catches up missed week`() = runTest {
        // Last allowance: 2024-01-01 (Monday). Now: 2024-01-15 (Monday, 2 weeks later).
        val lastPaid = instantOf(2024, 1, 1)
        val now = instantOf(2024, 1, 15)
        val child = makeChild(allowanceDayOfWeek = DayOfWeek.MONDAY)
        val txRepo = FakeTransactionRepository().also {
            it.lastAllowanceMs = lastPaid.toEpochMilliseconds()
        }
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        // Should insert for Jan 8 AND Jan 15 (2 missed Mondays)
        assertThat(txRepo.inserted).hasSize(2)
    }

    @Test
    fun `skips inactive child`() = runTest {
        val now = instantOf(2024, 1, 8)
        val child = makeChild(active = false)
        val txRepo = FakeTransactionRepository()
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        assertThat(txRepo.inserted).isEmpty()
    }

    @Test
    fun `skips child with zero allowance`() = runTest {
        val now = instantOf(2024, 1, 8)
        val child = makeChild(allowanceCents = 0L)
        val txRepo = FakeTransactionRepository()
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        assertThat(txRepo.inserted).isEmpty()
    }

    @Test
    fun `first run inserts at most one allowance within past 7 days`() = runTest {
        // Now is Thursday 2024-01-11. Monday was Jan 8 (3 days ago, within 7-day window).
        val now = instantOf(2024, 1, 11)
        val child = makeChild(allowanceDayOfWeek = DayOfWeek.MONDAY)
        val txRepo = FakeTransactionRepository() // no previous allowance
        val useCase = ProcessDueAllowancesUseCase(
            childRepository = FakeChildRepository(listOf(child)),
            transactionRepository = txRepo,
            clock = FakeClock(now),
        )

        useCase("argent de poche")

        // Jan 8 is Monday and within past 7 days (Jan 5–11), so one insertion expected
        assertThat(txRepo.inserted).hasSize(1)
    }
}
