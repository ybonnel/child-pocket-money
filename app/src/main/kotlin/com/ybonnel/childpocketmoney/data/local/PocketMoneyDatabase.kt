package com.ybonnel.childpocketmoney.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ybonnel.childpocketmoney.data.local.dao.ChildDao
import com.ybonnel.childpocketmoney.data.local.dao.TransactionDao
import com.ybonnel.childpocketmoney.data.local.entity.ChildEntity
import com.ybonnel.childpocketmoney.data.local.entity.TransactionEntity

@Database(
    entities = [ChildEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PocketMoneyDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun transactionDao(): TransactionDao
}
