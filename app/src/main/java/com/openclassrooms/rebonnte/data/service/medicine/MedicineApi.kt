package com.openclassrooms.rebonnte.data.service.medicine

import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining file-related operations.
 * Implementations handle storage, upload and retrieval logic.
 */
interface MedicineApi {

    /** Returns all medicines ordered by creation date. */
    fun getHistoryOrderByCreationDateDesc(): Flow<List<Medicine>>

    /** Uploads a medicine and returns the list of uploaded URLs. */
    suspend fun addMedicine(medicine: Medicine)

    /** Observes a medicine by its ID and user ID. */
    fun getMedicineById(medicineId: String): Flow<Medicine?>
}