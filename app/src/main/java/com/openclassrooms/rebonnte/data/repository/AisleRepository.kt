package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.aisle.AisleApi
import com.openclassrooms.rebonnte.domain.model.Aisle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository responsible for managing aisle-related operations.
 * It delegates data access to the [AisleApi] (FirebaseAisleApi implementation),
 * providing a clean abstraction layer for ViewModels.
 */
class AisleRepository @Inject constructor(
    private val aisleApi: AisleApi
) {

    fun getAllAisles(searchQuery: String = ""): Flow<List<Aisle>> = aisleApi.getAisles(searchQuery)
    suspend fun addAisle(aisle: Aisle) = aisleApi.addAisle(aisle)
    suspend fun deleteAisles(ids: Set<String>) = aisleApi.deleteAisles(ids)
    fun getAisleById(aisleId: String): Flow<Aisle> =
        aisleApi.getAisleById(aisleId)
}