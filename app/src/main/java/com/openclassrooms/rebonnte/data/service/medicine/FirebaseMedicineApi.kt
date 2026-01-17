package com.openclassrooms.rebonnte.data.service.medicine

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.rebonnte.data.dto.MedicineDto
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.MedicineOrderField
import com.openclassrooms.rebonnte.domain.model.MedicineSort
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Firebase implementation of [MedicineApi].
 * Handles file uploads to Firebase Storage and metadata persistence in Firestore.
 */
class FirebaseMedicineApi @Inject constructor() : MedicineApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val medicinesCollection = firestore.collection("medicines")

    override fun getAllMedicines(
        sort: MedicineSort,
        searchQuery: String
    ): Flow<List<Medicine>> {

        Log.e("REPO_CALL", "sort=$sort query=$searchQuery")

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
    }

    override suspend fun addMedicine(medicine: Medicine): Result<Unit> {
        return try {
            val docRef = medicinesCollection.document()
            val dto = medicine.copy(
                id = docRef.id,
                nameLowercase = medicine.name.lowercase()
            ).toDto()
            docRef.set(dto)
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("FirebaseMedicineApi", "Error while adding medicine", e)
            Result.failure(e)
        }
    }

    override suspend fun editMedicine(medicine: Medicine): Result<Unit> {
        return try {
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

    override fun getMedicineById(medicineId: String): Flow<Medicine> {
        return medicinesCollection
            .whereEqualTo("id", medicineId)
            .limit(1)
            .dataObjects<MedicineDto>()
            .map { Medicine.fromDto(it.first()) }
    }

    override fun getMedicinesByAisle(aisleId: String, searchQuery: String): Flow<List<Medicine>> {
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
            .map { list ->
                list.map { Medicine.fromDto(it) }
            }
    }

    /**
     * Deletes the current medicine from Firestore.
     *
     * @return A [Result] indicating whether the deletion was successful.
     */
    override suspend fun deleteMedicines(ids: Set<String>): Result<Unit> {
        return try {
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