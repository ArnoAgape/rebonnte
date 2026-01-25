package com.openclassrooms.rebonnte.data.service.medicine

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.MedicineDto
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.MedicineSort
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Firebase implementation of [MedicineApi].
 * Responsible for accessing and manipulating medicines stored in Firestore.
 */
class FirebaseMedicineApi @Inject constructor() : MedicineApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val medicinesCollection = firestore.collection("medicines")

    /**
     * Retrieves all medicines with optional search and sorting.
     *
     * Data fetching and transformations are executed on an IO thread
     * to avoid blocking the main thread.
     */
    override fun getAllMedicines(
        sort: MedicineSort,
        searchQuery: String
    ): Flow<List<Medicine>> {
        val q = searchQuery.trim().lowercase()

        val baseQuery = if (q.isBlank()) {
            medicinesCollection
        } else {
            medicinesCollection
                .orderBy("nameLowercase")
                .startAt(q)
                .endAt(q + "\uf8ff")
        }

        return baseQuery
            .dataObjects<MedicineDto>()
            .map { dto ->
                sort.sort(dto.map { Medicine.fromDto(it) })
            }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Adds a new medicine to Firestore.
     *
     * This operation performs a network write and is executed on an IO thread.
     */
    override suspend fun addMedicine(medicine: Medicine): Result<Medicine> =
        withContext(Dispatchers.IO) {
            try {
                val docRef = medicinesCollection.document()
                val savedMedicine = medicine.copy(
                    id = docRef.id,
                    nameLowercase = medicine.name.lowercase()
                )
                docRef.set(savedMedicine.toDto())
                Result.success(savedMedicine)

            } catch (e: Exception) {
                Log.e("FirebaseMedicineApi", "Error while adding medicine", e)
                Result.failure(e)
            }
        }

    /**
     * Updates an existing medicine in Firestore.
     *
     * Network access is executed on an IO dispatcher.
     */
    override suspend fun editMedicine(medicine: Medicine): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dto = medicine.copy(
                    nameLowercase = medicine.name.lowercase()
                ).toDto()

                medicinesCollection
                    .document(medicine.id)
                    .set(dto)

                Result.success(Unit)

            } catch (e: Exception) {
                Log.e("FirebaseMedicineApi", "Error while editing medicine", e)
                Result.failure(e)
            }
        }

    /**
     * Retrieves a single medicine by its identifier.
     *
     * Data collection and mapping are executed on an IO thread.
     */
    override fun getMedicineById(medicineId: String): Flow<Medicine> {
        return medicinesCollection
            .whereEqualTo("id", medicineId)
            .limit(1)
            .dataObjects<MedicineDto>()
            .map { Medicine.fromDto(it.first()) }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Retrieves medicines belonging to a specific aisle, with optional search and sorting.
     *
     * Filtering, mapping and sorting are executed on an IO dispatcher.
     */
    override fun getMedicinesByAisle(
        sort: MedicineSort,
        aisleId: String,
        searchQuery: String
    ): Flow<List<Medicine>> {
        val q = searchQuery.trim().lowercase()

        val query = if (q.isBlank()) {
            medicinesCollection
                .whereEqualTo("aisleId", aisleId)
                .orderBy("nameLowercase")
        } else {
            medicinesCollection
                .whereEqualTo("aisleId", aisleId)
                .orderBy("nameLowercase")
                .startAt(q)
                .endAt(q + "\uf8ff")
        }

        return query
            .dataObjects<MedicineDto>()
            .map { dto ->
                sort.sort(dto.map { Medicine.fromDto(it) })
            }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Deletes multiple medicines from Firestore.
     *
     * Each deletion is a network operation executed on an IO thread.
     */
    override suspend fun deleteMedicines(ids: Set<String>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                ids.forEach { id ->
                    if (id.isBlank()) error("Medicine ID empty")
                    medicinesCollection.document(id).delete()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

}