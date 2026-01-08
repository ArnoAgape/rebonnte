package com.openclassrooms.rebonnte.di

import com.openclassrooms.rebonnte.data.service.aisle.AisleApi
import com.openclassrooms.rebonnte.data.service.aisle.FirebaseAisleApi
import com.openclassrooms.rebonnte.data.service.history.FirebaseHistoryApi
import com.openclassrooms.rebonnte.data.service.history.HistoryApi
import com.openclassrooms.rebonnte.data.service.medicine.MedicineApi
import com.openclassrooms.rebonnte.data.service.medicine.FirebaseMedicineApi
import com.openclassrooms.rebonnte.data.service.user.FirebaseUserApi
import com.openclassrooms.rebonnte.data.service.user.UserApi
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
     * Provides a singleton [MedicineApi] implementation backed by Firebase.
     */
    @Provides
    @Singleton
    fun provideMedicineApi(firebaseMedicineApi: FirebaseMedicineApi): MedicineApi = firebaseMedicineApi

    /**
     * Provides a singleton [UserApi] implementation backed by Firebase.
     */
    @Provides
    @Singleton
    fun provideUserApi(): UserApi = FirebaseUserApi()

    /**
     * Provides a singleton [AisleApi] implementation backed by Firebase.
     */
    @Provides
    @Singleton
    fun provideAisleApi(firebaseAisleApi: FirebaseAisleApi): AisleApi = firebaseAisleApi

    /**
     * Provides a singleton [HistoryApi] implementation backed by Firebase.
     */
    @Provides
    @Singleton
    fun provideHistoryApi(): HistoryApi = FirebaseHistoryApi()
}