package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.medicine.MedicineApi
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.MedicineSort
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
    private val medicineApi: MedicineApi,
    private val historyRepository: HistoryRepository
) {

    fun getAllMedicines(sort: MedicineSort, searchQuery: String): Flow<List<Medicine>> =
        medicineApi.getAllMedicines(
            sort = sort,
            searchQuery = searchQuery
        )

    suspend fun addMedicine(medicine: Medicine) = medicineApi.addMedicine(medicine)
    suspend fun editMedicine(medicine: Medicine) = medicineApi.editMedicine(medicine)
    suspend fun deleteMedicines(ids: Set<String>): Result<Unit> =
        try {
            ids.forEach { medicineId ->
                historyRepository
                    .deleteHistory(medicineId)
                    .getOrThrow()

                medicineApi
                    .deleteMedicines(setOf(medicineId))
                    .getOrThrow()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    fun getMedicineById(medicineId: String): Flow<Medicine?> = medicineApi.getMedicineById(medicineId)
    fun getMedicinesByAisle(sort: MedicineSort, aisleId: String, searchQuery: String): Flow<List<Medicine>> =
        medicineApi.getMedicinesByAisle(sort, aisleId, searchQuery)
}