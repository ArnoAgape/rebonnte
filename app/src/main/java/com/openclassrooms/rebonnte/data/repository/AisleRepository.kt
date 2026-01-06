package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.aisle.AisleApi
import com.openclassrooms.rebonnte.domain.model.Aisle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AisleRepository @Inject constructor(
    private val aisleApi: AisleApi
) {

    /**
     * Observes the list of all files ordered by descending creation date.
     */
    val aisles: Flow<List<Aisle>> = aisleApi.getAllAisles()

    /**
     * Uploads a new file to Firebase (Storage + Firestore).
     * @throws java.io.IOException if there is no internet connection
     * @throws IllegalArgumentException if the file type or size is invalid
     */
    suspend fun addAisle(aisle: Aisle): Unit = aisleApi.addAisle(aisle)

    /**
     * Observes a single file by its unique ID.
     */
    fun getAisleById(aisleId: String): Flow<Aisle?> =
        aisleApi.getAisleById(aisleId)
}