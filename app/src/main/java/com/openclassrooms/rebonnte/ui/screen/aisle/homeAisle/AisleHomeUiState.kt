package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import com.openclassrooms.rebonnte.domain.model.Aisle

sealed class AisleHomeUiState {
    object Idle : AisleHomeUiState()
    object Loading : AisleHomeUiState()
    data class Success(val aisles: List<Aisle>) : AisleHomeUiState()
    sealed class Error : AisleHomeUiState() {
        data class Empty(val message: String = "No medicines found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}