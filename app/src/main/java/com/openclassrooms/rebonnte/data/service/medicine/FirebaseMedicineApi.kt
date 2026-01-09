package com.openclassrooms.rebonnte.data.service.medicine

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.MedicineDto
import com.openclassrooms.rebonnte.domain.model.Medicine
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Firebase implementation of [MedicineApi].
 * Handles file uploads to Firebase Storage and metadata persistence in Firestore.
 */
class FirebaseMedicineApi @Inject constructor() : MedicineApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val medicinesCollection = firestore.collection("medicines")

    override fun getMedicinesOrderByNameAsc(): Flow<List<Medicine>> {
        return medicinesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .dataObjects<MedicineDto>()
            .map { list -> list.map { Medicine.fromDto(it) } }
    }

    override suspend fun addMedicine(medicine: Medicine) {
        try {
            val docRef = medicinesCollection.document()
            val dto = medicine.copy(id = docRef.id).toDto()
            docRef.set(dto).await()

        } catch (e: Exception) {
            Log.e("FirebaseMedicineApi", "Error while adding medicine", e)
            throw e
        }
    }

    override fun getMedicineById(medicineId: String): Flow<Medicine?> {
        return medicinesCollection
            .whereEqualTo("id", medicineId)
            .limit(1)
            .dataObjects<MedicineDto>()
            .map { Medicine.fromDto(it.first()) }
    }

    override fun getMedicinesByAisle(aisleId: String): Flow<List<Medicine>> {
        return medicinesCollection
            .whereEqualTo("aisleId", aisleId)
            .orderBy("aisleName", Query.Direction.ASCENDING)
            .dataObjects<MedicineDto>()
            .map { list ->
                Log.d("DEBUG_MEDICINES_BY_AISLE", "found ${list.size} medicines for aisle=$aisleId")
                list.map { Medicine.fromDto(it) } }
    }

}