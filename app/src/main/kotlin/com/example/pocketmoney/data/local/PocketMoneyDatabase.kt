package com.example.pocketmoney.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pocketmoney.data.local.dao.ChildDao
import com.example.pocketmoney.data.local.dao.TransactionDao
import com.example.pocketmoney.data.local.entity.ChildEntity
import com.example.pocketmoney.data.local.entity.TransactionEntity

@Database(
    entities = [ChildEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PocketMoneyDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun transactionDao(): TransactionDao
}
