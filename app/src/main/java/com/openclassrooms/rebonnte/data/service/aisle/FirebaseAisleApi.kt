package com.openclassrooms.rebonnte.data.service.aisle

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.AisleDto
import com.openclassrooms.rebonnte.domain.model.Aisle
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Firebase implementation of [AisleApi].
 * Responsible for accessing and manipulating aisles stored in Firestore.
 */
class FirebaseAisleApi @Inject constructor() : AisleApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val aislesCollection = firestore.collection("aisles")

    /**
     * Retrieves all aisles with optional search.
     *
     * Data fetching and transformations are executed on an IO thread
     * to avoid blocking the main thread.
     */
    override fun getAisles(searchQuery: String): Flow<List<Aisle>> {

        val q = searchQuery.trim().lowercase()

        val query = aislesCollection
            .orderBy("nameLowercase")
            .let {
                if (q.isBlank()) it
                else it.startAt(q).endAt(q + "\uf8ff")
            }

        return query
            .dataObjects<AisleDto>()
            .map { list -> list.map { Aisle.fromDto(it) } }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Adds a new aisle to Firestore.
     *
     * This operation performs a network write and is executed on an IO thread.
     */
    override suspend fun addAisle(aisle: Aisle): Result<Unit> =
        withContext(Dispatchers.IO) {
        try {
            val docRef = aislesCollection.document()

            val dto = aisle.copy(
                id = docRef.id,
                nameLowercase = aisle.name.lowercase()
            ).toDto()
            docRef.set(dto)
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("FirebaseAisleApi", "Error while adding aisle", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a single aisle by its identifier.
     *
     * Data collection and mapping are executed on an IO thread.
     */
    override fun getAisleById(aisleId: String): Flow<Aisle?> {
        return aislesCollection
            .whereEqualTo("id", aisleId)
            .limit(1)
            .dataObjects<AisleDto>()
            .map { list ->
                if (list.isNotEmpty()) {
                    Aisle.fromDto(list.first())
                } else {
                    throw IllegalStateException("Aisle not found for id=$aisleId")
                }
            }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Deletes multiple aisles from Firestore.
     *
     * Each deletion is a network operation executed on an IO thread.
     */
    override suspend fun deleteAisles(ids: Set<String>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                ids.forEach { id ->
                    if (id.isBlank()) error("Aisle ID empty")
                    aislesCollection.document(id).delete()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

}