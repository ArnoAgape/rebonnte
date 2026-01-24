package com.openclassrooms.rebonnte.data.service.history

import com.openclassrooms.rebonnte.domain.model.History
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining history-related data operations.
 *
 * Provides real-time observation and creation of history entries
 * associated with medicines.
 */
interface HistoryApi {

    fun observeHistory(medicineId: String): Flow<List<History>>
    suspend fun addHistory(medicineId: String, history: History): Result<Unit>
}