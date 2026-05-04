package com.example.pocketmoney.di

import com.example.pocketmoney.data.repository.ChildRepositoryImpl
import com.example.pocketmoney.data.repository.TransactionRepositoryImpl
import com.example.pocketmoney.domain.repository.ChildRepository
import com.example.pocketmoney.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChildRepository(impl: ChildRepositoryImpl): ChildRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
}
