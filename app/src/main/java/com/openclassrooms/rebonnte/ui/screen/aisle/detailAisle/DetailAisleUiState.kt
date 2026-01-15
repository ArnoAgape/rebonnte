package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class DetailAisleUiState {
    object Loading : DetailAisleUiState()
    data class Success(
        val aisle: Aisle,
        val medicines: List<Medicine>
    ) : DetailAisleUiState()

    sealed class Error : DetailAisleUiState() {
        data class Empty(val message: String = "No medicines found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}