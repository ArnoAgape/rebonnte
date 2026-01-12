package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.domain.model.History

sealed class HistoryDetailUiState {
    object Loading : HistoryDetailUiState()
    data class Success(val history: List<History>) : HistoryDetailUiState()
    sealed class Error : HistoryDetailUiState() {
        data class Empty(val message: String = "No history found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}