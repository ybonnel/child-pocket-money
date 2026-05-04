package com.example.pocketmoney.data.repository

import com.example.pocketmoney.data.local.dao.TransactionDao
import com.example.pocketmoney.data.mapper.toDomain
import com.example.pocketmoney.data.mapper.toEntity
import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun observeByChild(childId: Long): Flow<List<Transaction>> =
        dao.observeByChild(childId).map { entities -> entities.map { it.toDomain() } }

    override fun observeBalance(childId: Long): Flow<Long> =
        dao.observeBalance(childId)

    override suspend fun lastAllowanceEpochMs(childId: Long): Long? =
        dao.lastAllowanceEpochMs(childId)

    override suspend fun insert(transaction: Transaction): Long =
        dao.insert(transaction.toEntity())

    override suspend fun delete(transaction: Transaction) =
        dao.delete(transaction.toEntity())
}
