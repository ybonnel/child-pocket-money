package com.ybonnel.childpocketmoney.data.repository

import com.ybonnel.childpocketmoney.data.local.dao.TransactionDao
import com.ybonnel.childpocketmoney.data.mapper.toDomain
import com.ybonnel.childpocketmoney.data.mapper.toEntity
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
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
