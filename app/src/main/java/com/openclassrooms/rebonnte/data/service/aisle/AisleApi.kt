package com.openclassrooms.rebonnte.data.service.aisle

import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

interface AisleApi {

    /** Returns all aisles ordered by name. */
    fun getAllAisles(): Flow<List<Aisle>>

    /** Uploads an aisle and returns the list of uploaded URLs. */
    suspend fun addAisle(aisle: Aisle)

    /** Observes a aisle by its ID */
    fun getAisleById(aisleId: String): Flow<Aisle>
}