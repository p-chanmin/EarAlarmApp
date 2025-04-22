package kr.ac.tukorea.android.earalarm.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.ac.tukorea.android.earalarm.data.datastore.DataStoreHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

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
    fun provideDataStore(dataStore: DataStore<Preferences>, gson: Gson): DataStoreHelper =
        DataStoreHelper(dataStore, gson)

    companion object {
        private const val PREFERENCES_STORE_NAME = "EarAlarmDataStore"
    }
}