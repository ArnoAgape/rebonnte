package com.openclassrooms.rebonnte.di

import com.openclassrooms.rebonnte.data.service.Api
import com.openclassrooms.rebonnte.data.service.FirebaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing application-wide dependencies.
 * Installed in [SingletonComponent] to ensure single instances
 * across the whole app lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     * Provides a singleton [Api] implementation backed by Firebase.
     */
    @Provides
    @Singleton
    fun provideFileApi(firebaseApi: FirebaseApi): Api = firebaseApi
}