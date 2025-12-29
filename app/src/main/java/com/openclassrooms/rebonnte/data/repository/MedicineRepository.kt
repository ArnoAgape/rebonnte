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
     * Observes the list of all files ordered by descending creation date.
     */
    val medicines: Flow<List<Medicine>> = medicineApi.getHistoryOrderByCreationDateDesc()

    /**
     * Uploads a new file to Firebase (Storage + Firestore).
     * @throws java.io.IOException if there is no internet connection
     * @throws IllegalArgumentException if the file type or size is invalid
     */
    suspend fun addMedicine(medicine: Medicine): Unit = medicineApi.addMedicine(medicine)

    /**
     * Observes a single file by its unique ID.
     */
    fun getMedicineById(medicineId: String): Flow<Medicine?> =
        medicineApi.getMedicineById(medicineId)
}