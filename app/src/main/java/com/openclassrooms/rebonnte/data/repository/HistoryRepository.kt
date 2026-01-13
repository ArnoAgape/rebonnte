package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.history.HistoryApi
import com.openclassrooms.rebonnte.domain.model.History
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyApi: HistoryApi
) {
    fun observeHistory(medicineId: String): Flow<List<History>> =
        historyApi.observeHistory(medicineId)

    suspend fun addHistory(medicineId: String, history: History): Result<Unit> =
        historyApi.addHistory(medicineId, history)
}