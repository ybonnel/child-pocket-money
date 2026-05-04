package com.ybonnel.childpocketmoney.data.repository

import com.ybonnel.childpocketmoney.data.local.dao.ChildDao
import com.ybonnel.childpocketmoney.data.mapper.toDomain
import com.ybonnel.childpocketmoney.data.mapper.toEntityForInsert
import com.ybonnel.childpocketmoney.data.mapper.toEntityForUpdate
import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        dao.insert(child.toEntityForInsert())

    override suspend fun update(child: Child) {
        // Fetch the existing entity to preserve the original createdAtEpochMs.
        val existing = dao.observeById(child.id).first()
        val createdAt = existing?.createdAtEpochMs ?: System.currentTimeMillis()
        dao.update(child.toEntityForUpdate(createdAt))
    }

    override suspend fun archive(id: Long) =
        dao.archive(id)
}
