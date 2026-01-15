package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.domain.model.History

sealed class DetailHistoryUiState {
    object Loading : DetailHistoryUiState()
    data class Success(val history: List<History>) : DetailHistoryUiState()
    sealed class Error : DetailHistoryUiState() {
        data class Empty(val message: String = "No history found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}