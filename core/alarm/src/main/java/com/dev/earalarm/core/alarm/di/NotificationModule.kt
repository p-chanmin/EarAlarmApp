package com.dev.earalarm.core.alarm.di

import android.content.Context
import com.dev.earalarm.core.alarm.EarAlarmNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ) = EarAlarmNotificationManager(context)
}