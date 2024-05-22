package kr.ac.tukorea.android.earalarm.presentation.alarm.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.ac.tukorea.android.earalarm.data.datastore.DataStoreHelper
import kr.ac.tukorea.android.earalarm.presentation.alarm.AlarmHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AlarmModule {

    @Singleton
    @Provides
    fun provideAlarmHelper(
        dataStoreHelper: DataStoreHelper,
        @ApplicationContext context: Context,
    ) = AlarmHelper(dataStoreHelper, context)
}