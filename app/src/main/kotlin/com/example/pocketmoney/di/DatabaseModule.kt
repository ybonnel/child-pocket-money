package com.example.pocketmoney.di

import android.content.Context
import androidx.room.Room
import com.example.pocketmoney.data.local.PocketMoneyDatabase
import com.example.pocketmoney.data.local.dao.ChildDao
import com.example.pocketmoney.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PocketMoneyDatabase =
        Room.databaseBuilder(
            context,
            PocketMoneyDatabase::class.java,
            "pocket_money.db"
        ).build()

    @Provides
    fun provideChildDao(db: PocketMoneyDatabase): ChildDao = db.childDao()

    @Provides
    fun provideTransactionDao(db: PocketMoneyDatabase): TransactionDao = db.transactionDao()
}
