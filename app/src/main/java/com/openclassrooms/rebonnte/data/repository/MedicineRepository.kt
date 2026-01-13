package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.medicine.MedicineApi
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for managing medicine-related operations.
 * It delegates data access to the [MedicineApi] (FirebaseMedicineApi implementation),
 * providing a clean abstraction layer for ViewModels.
 */
@Singleton
class MedicineRepository @Inject constructor(
    private val medicineApi: MedicineApi
) {

    /**
     * Observes the list of all medicines ordered by ascending name.
     */
    val medicines: Flow<List<Medicine>> = medicineApi.getMedicinesOrderByNameAsc()

    /**
     * Uploads a new medicine to Firebase (Storage + Firestore).
     */
    suspend fun addMedicine(medicine: Medicine) = medicineApi.addMedicine(medicine)

    /**
     * Edits an existing medicine to Firebase (Storage + Firestore).
     */
    suspend fun editMedicine(medicine: Medicine) = medicineApi.editMedicine(medicine)

    suspend fun deleteMedicines(ids: Set<String>) = medicineApi.deleteMedicines(ids)

    /**
     * Observes a single medicine by its unique ID.
     */
    fun getMedicineById(medicineId: String): Flow<Medicine?> =
        medicineApi.getMedicineById(medicineId)

    fun getMedicinesByAisle(aisleId: String): Flow<List<Medicine>> =
        medicineApi.getMedicinesByAisle(aisleId)
}