package com.ybonnel.childpocketmoney.domain.repository

import com.ybonnel.childpocketmoney.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeByChild(childId: Long): Flow<List<Transaction>>
    fun observeBalance(childId: Long): Flow<Long>
    suspend fun lastAllowanceEpochMs(childId: Long): Long?
    suspend fun insert(transaction: Transaction): Long
    suspend fun delete(transaction: Transaction)
}
