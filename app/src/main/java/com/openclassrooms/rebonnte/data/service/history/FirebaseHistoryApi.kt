package com.openclassrooms.rebonnte.data.service.history

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.HistoryDto
import com.openclassrooms.rebonnte.domain.model.History
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Firebase implementation of [HistoryApi].
 *
 * Provides real-time access to medicine history entries stored in Firestore.
 * This class listens for changes in the history subcollection and maps
 * Firestore DTOs to domain models.
 */
class FirebaseHistoryApi @Inject constructor() : HistoryApi {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observes the history of a medicine in real time.
     *
     * Emits an ordered list of history entries whenever changes occur
     * in the medicine's history collection.
     *
     * @param medicineId The unique identifier of the medicine.
     * @return A [Flow] of history entries ordered by date.
     */
    override fun observeHistory(medicineId: String): Flow<List<History>> {
        return firestore.collection("medicines")
            .document(medicineId)
            .collection("histories")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .dataObjects<HistoryDto>()
            .map { list -> list.map { History.fromDto(it) } }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Adds a new history to Firestore.
     *
     * This operation performs a network write and is executed on an IO thread.
     */
    override suspend fun addHistory(medicineId: String, history: History): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val docRef = firestore.collection("medicines")
                    .document(medicineId)
                    .collection("histories")
                    .document()

                val dto = history.copy(id = docRef.id).toDto()
                docRef.set(dto)
                Result.success(Unit)

            } catch (e: Exception) {
                Log.e("FirebaseHistoryApi", "Error while adding history", e)
                Result.failure(e)
            }
        }
}