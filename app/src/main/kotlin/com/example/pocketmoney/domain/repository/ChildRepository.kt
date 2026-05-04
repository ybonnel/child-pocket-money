package com.example.pocketmoney.domain.repository

import com.example.pocketmoney.domain.model.Child
import kotlinx.coroutines.flow.Flow

interface ChildRepository {
    fun observeAll(): Flow<List<Child>>
    fun observeById(id: Long): Flow<Child?>
    suspend fun insert(child: Child): Long
    suspend fun update(child: Child)
    suspend fun archive(id: Long)
}
