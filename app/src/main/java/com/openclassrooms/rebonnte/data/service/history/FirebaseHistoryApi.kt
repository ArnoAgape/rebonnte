package com.openclassrooms.rebonnte.data.service.history

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.HistoryDto
import com.openclassrooms.rebonnte.domain.model.History
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseHistoryApi @Inject constructor() : HistoryApi {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observes the history for a given Medicine in real time.
     *
     * Uses a Firestore snapshot listener to emit updates whenever medicines
     * are added, modified, or removed.
     *
     * @param medicineId The unique identifier of the Post.
     * @return A [Flow] emitting ordered lists of History.
     */
    override fun observeHistory(medicineId: String): Flow<List<History>> {
        return firestore.collection("medicines")
            .document(medicineId)
            .collection("histories")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .dataObjects<HistoryDto>()
            .map { list -> list.map { History.fromDto(it) } }
    }

    override suspend fun addHistory(medicineId: String, history: History): Result<Unit> {
        return try {
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