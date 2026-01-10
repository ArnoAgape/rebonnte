package com.openclassrooms.rebonnte.data.service.aisle

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.AisleDto
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseAisleApi @Inject constructor() : AisleApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val aislesCollection = firestore.collection("aisles")

    override fun getAislesOrderByNameAsc(): Flow<List<Aisle>> {
        return aislesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .dataObjects<AisleDto>()
            .map { list -> list.map { Aisle.fromDto(it) } }
    }

    override suspend fun addAisle(aisle: Aisle) {
        try {
            val docRef = aislesCollection.document()

            val dto = aisle.copy(id = docRef.id).toDto()

            docRef.set(dto).await()

        } catch (e: Exception) {
            Log.e("FirebaseAisleApi", "Error while adding aisle", e)
            throw e
        }
    }

    override fun getAisleById(aisleId: String): Flow<Aisle> {
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
    }

    /**
     * Deletes the current aisle from Firestore.
     *
     * @return A [Result] indicating whether the deletion was successful.
     */
    override suspend fun deleteAisle(aisleId: String): Result<Unit> {
        return try {
            if (aisleId.isBlank()) {
                return Result.failure(IllegalArgumentException("Aisle ID is empty"))
            }

            aislesCollection
                .document(aisleId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}