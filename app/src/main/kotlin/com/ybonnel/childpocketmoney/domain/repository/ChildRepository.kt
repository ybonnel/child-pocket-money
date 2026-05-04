package com.ybonnel.childpocketmoney.domain.repository

import com.ybonnel.childpocketmoney.domain.model.Child
import kotlinx.coroutines.flow.Flow

interface ChildRepository {
    fun observeAll(): Flow<List<Child>>
    fun observeById(id: Long): Flow<Child?>
    suspend fun insert(child: Child): Long
    suspend fun update(child: Child)
    suspend fun archive(id: Long)
}
