package com.openclassrooms.rebonnte.data.service.history

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.domain.model.History
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class FirebaseHistoryApi @Inject constructor(
) : HistoryApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val historyCollection = firestore.collection("history")

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
        return historyCollection
            .document(medicineId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .dataObjects()
    }
}