package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class HistoryUiState {
    object Idle : HistoryUiState()
    object Loading : HistoryUiState()
    data class Success(val medicines: List<Medicine>) : HistoryUiState()
    sealed class Error : HistoryUiState() {
        data class Empty(val message: String = "No history found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}