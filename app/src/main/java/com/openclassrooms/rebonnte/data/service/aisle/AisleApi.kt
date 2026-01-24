package com.openclassrooms.rebonnte.data.service.aisle

import com.openclassrooms.rebonnte.domain.model.Aisle
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining aisle-related data operations.
 *
 * Provides access to aisle retrieval, creation,
 * deletion, and real-time observation.
 */
interface AisleApi {

    fun getAisles(searchQuery: String): Flow<List<Aisle>>
    suspend fun addAisle(aisle: Aisle): Result<Unit>
    fun getAisleById(aisleId: String): Flow<Aisle?>
    suspend fun deleteAisles(ids: Set<String>): Result<Unit>
}