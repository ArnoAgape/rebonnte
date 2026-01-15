package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import com.openclassrooms.rebonnte.domain.model.Aisle

sealed class HomeAisleUiState {
    object Loading : HomeAisleUiState()
    data class Success(val aisles: List<Aisle>) : HomeAisleUiState()
    sealed class Error : HomeAisleUiState() {
        data class Empty(val message: String = "No aisle found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}