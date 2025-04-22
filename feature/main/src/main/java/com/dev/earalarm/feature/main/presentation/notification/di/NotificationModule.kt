package kr.ac.tukorea.android.earalarm.presentation.notification.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.ac.tukorea.android.earalarm.presentation.notification.NotificationHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationHelper(
        @ApplicationContext context: Context,
    ) = NotificationHelper(context)
}