package com.example.pocketmoney.data.repository

import com.example.pocketmoney.data.local.dao.ChildDao
import com.example.pocketmoney.data.mapper.toDomain
import com.example.pocketmoney.data.mapper.toEntity
import com.example.pocketmoney.domain.model.Child
import com.example.pocketmoney.domain.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChildRepositoryImpl @Inject constructor(
    private val dao: ChildDao
) : ChildRepository {

    override fun observeAll(): Flow<List<Child>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Child?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun insert(child: Child): Long =
        dao.insert(child.toEntity())

    override suspend fun update(child: Child) =
        dao.update(child.toEntity())

    override suspend fun archive(id: Long) =
        dao.archive(id)
}
