package com.ybonnel.childpocketmoney.di

import com.ybonnel.childpocketmoney.data.repository.ChildRepositoryImpl
import com.ybonnel.childpocketmoney.data.repository.TransactionRepositoryImpl
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import com.ybonnel.childpocketmoney.domain.repository.TransactionRepository
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
