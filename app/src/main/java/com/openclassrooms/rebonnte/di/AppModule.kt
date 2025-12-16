package com.openclassrooms.rebonnte.di

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
}