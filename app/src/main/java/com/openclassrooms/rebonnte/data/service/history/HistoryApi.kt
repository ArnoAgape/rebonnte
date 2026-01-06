package com.openclassrooms.rebonnte.data.service.history

import com.openclassrooms.rebonnte.domain.model.History
import kotlinx.coroutines.flow.Flow

interface HistoryApi {
    fun observeHistory(medicineId: String): Flow<List<History>>
}