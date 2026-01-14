package com.openclassrooms.rebonnte.data.service.medicine

import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.MedicineOrderField
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining file-related operations.
 * Implementations handle storage, upload and retrieval logic.
 */
interface MedicineApi {

    /** Returns all medicines ordered by name. */
    fun getMedicinesOrderBy(field: MedicineOrderField,direction: Query.Direction): Flow<List<Medicine>>

    /** Uploads a medicine. */
    suspend fun addMedicine(medicine: Medicine): Result<Unit>

    /** Edits a medicine. */
    suspend fun editMedicine(medicine: Medicine): Result<Unit>

    /** Observes a medicine by its ID. */
    fun getMedicineById(medicineId: String): Flow<Medicine?>

    fun getMedicinesByAisle(aisleId: String): Flow<List<Medicine>>

    /**
     * Permanently deletes the current medicine.
     *
     * @return A [Result] indicating whether the deletion was successful or not.
     */
    suspend fun deleteMedicines(ids: Set<String>): Result<Unit>
}