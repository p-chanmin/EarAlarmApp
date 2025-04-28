package com.dev.earalarm.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.dev.earalarm.core.data.TimerRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_STORE_NAME) }
        )

    @Provides
    @Singleton
    fun provideTimerRepository(dataStore: DataStore<Preferences>, gson: Gson): TimerRepository =
        TimerRepository(dataStore, gson)

    companion object {
        private const val PREFERENCES_STORE_NAME = "EarAlarmDataStore"
    }
}