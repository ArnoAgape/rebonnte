package com.openclassrooms.rebonnte.data.service.aisle

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.AisleDto
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.IOException

class FirebaseAisleApi @Inject constructor(
    private val networkUtils: NetworkUtils
) : AisleApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val aislesCollection = firestore.collection("aisles")

    override fun getAllAisles(): Flow<List<Aisle>> {
        return aislesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .dataObjects<AisleDto>()
            .map { list -> list.map { Aisle.fromDto(it) } }
    }

    override suspend fun addAisle(aisle: Aisle) {
        if (!networkUtils.isNetworkAvailable()) {
            throw IOException("No internet connection")
        }
        try {

            aislesCollection.document(aisle.id).set(aisle.toDto()).await()

        } catch (e: Exception) {
            Log.e("FirebaseFileApi", "Error while adding document", e)
            throw e
        }
    }

    override fun getAisleById(aisleId: String): Flow<Aisle> {
        return aislesCollection
            .whereEqualTo("id", aisleId)
            .limit(1)
            .dataObjects<AisleDto>()
            .map { Aisle.fromDto(it.first()) }
    }

}