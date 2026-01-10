package com.openclassrooms.rebonnte.data.service.medicine

import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining file-related operations.
 * Implementations handle storage, upload and retrieval logic.
 */
interface MedicineApi {

    /** Returns all medicines ordered by name. */
    fun getMedicinesOrderByNameAsc(): Flow<List<Medicine>>

    /** Uploads a medicine. */
    suspend fun addMedicine(medicine: Medicine)

    /** Edits a medicine. */
    suspend fun editMedicine(medicine: Medicine): Result<Unit>

    /** Observes a medicine by its ID. */
    fun getMedicineById(medicineId: String): Flow<Medicine?>

    fun getMedicinesByAisle(aisleId: String): Flow<List<Medicine>>
}