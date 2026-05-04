package com.example.pocketmoney.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.pocketmoney.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("""
        SELECT * FROM transactions
        WHERE childId = :childId
        ORDER BY occurredAtEpochMs DESC, id DESC
    """)
    fun observeByChild(childId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amountCents), 0) FROM transactions WHERE childId = :childId")
    fun observeBalance(childId: Long): Flow<Long>

    @Query("""
        SELECT MAX(occurredAtEpochMs) FROM transactions
        WHERE childId = :childId AND type = 'ALLOWANCE'
    """)
    suspend fun lastAllowanceEpochMs(childId: Long): Long?

    @Insert
    suspend fun insert(tx: TransactionEntity): Long

    @Delete
    suspend fun delete(tx: TransactionEntity)
}
