package com.openclassrooms.rebonnte.data.service.medicine

import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.MedicineSort
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining medicine-related data operations.
 *
 * Provides access to medicine retrieval, creation, update,
 * deletion, and real-time observation.
 */
interface MedicineApi {

    fun getAllMedicines(sort: MedicineSort, searchQuery: String): Flow<List<Medicine>>
    suspend fun addMedicine(medicine: Medicine): Result<Medicine>
    suspend fun editMedicine(medicine: Medicine): Result<Unit>
    fun getMedicineById(medicineId: String): Flow<Medicine>
    fun getMedicinesByAisle(sort: MedicineSort, aisleId: String, searchQuery: String): Flow<List<Medicine>>
    suspend fun deleteMedicines(ids: Set<String>): Result<Unit>
}