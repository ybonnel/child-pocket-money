package com.example.pocketmoney.di

import com.example.pocketmoney.core.time.Clock
import com.example.pocketmoney.core.time.SystemClock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ClockModule {

    @Binds
    @Singleton
    abstract fun bindClock(impl: SystemClock): Clock
}
