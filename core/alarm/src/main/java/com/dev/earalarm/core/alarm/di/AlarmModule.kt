package com.dev.earalarm.core.alarm.di

import android.content.Context
import com.dev.earalarm.core.alarm.EarAlarmManager
import com.dev.earalarm.core.data.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AlarmModule {

    @Singleton
    @Provides
    fun provideAlarmManager(
        timerRepository: TimerRepository,
        @ApplicationContext context: Context,
    ) = EarAlarmManager(timerRepository, context)
}