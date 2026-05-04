package com.ybonnel.childpocketmoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ybonnel.childpocketmoney.data.local.entity.ChildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    @Query("SELECT * FROM children WHERE archived = 0 ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE id = :id")
    fun observeById(id: Long): Flow<ChildEntity?>

    @Insert
    suspend fun insert(child: ChildEntity): Long

    @Update
    suspend fun update(child: ChildEntity)

    @Query("UPDATE children SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)
}
