package com.example.pocketmoney.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocketmoney.domain.usecase.balance.ProcessDueAllowancesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periodic worker that runs daily to process due allowances.
 * Uses the idempotent ProcessDueAllowancesUseCase.
 */
@HiltWorker
class WeeklyAllowanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val processAllowances: ProcessDueAllowancesUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            processAllowances()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "weekly_allowance_worker"
    }
}
