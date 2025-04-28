package com.dev.core.admob.di

import android.content.Context
import com.dev.core.admob.AdMobManager
import com.dev.core.admob.AdMobManagerImpl
import com.google.android.gms.ads.MobileAds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdMobModule {

    @Singleton
    @Provides
    fun provideAdMobManager(@ApplicationContext context: Context): AdMobManager {
        MobileAds.initialize(context)
        return AdMobManagerImpl(context)
    }
}