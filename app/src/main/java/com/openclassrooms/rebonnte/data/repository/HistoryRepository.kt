package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.history.HistoryApi
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.StockChangeType
import com.openclassrooms.rebonnte.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for managing medicine-related operations.
 * It delegates data access to the [HistoryApi] (FirebaseHistoryApi implementation),
 * providing a clean abstraction layer for ViewModels.
 */
@Singleton
class HistoryRepository @Inject constructor(
    private val historyApi: HistoryApi
) {
    fun observeHistory(medicineId: String): Flow<List<History>> =
        historyApi.observeHistory(medicineId)

    suspend fun addStockHistory(medicine: Medicine, delta: Int, author: User) {
        val history = History(
            medicineName = medicine.name,
            author = author,
            dateTime = Instant.now(),
            quantity = kotlin.math.abs(delta),
            changeType = if (delta >= 0)
                StockChangeType.ADDED
            else
                StockChangeType.REMOVED
        )

        historyApi.addHistory(medicine.id, history)
    }
    suspend fun deleteHistory(medicineId: String) = historyApi.deleteAllHistoryForMedicine(medicineId)
}