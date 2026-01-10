package com.openclassrooms.rebonnte.ui.screen.aisle.addAisle

import com.openclassrooms.rebonnte.domain.model.Aisle

sealed class AddAisleUiState {
    object Idle : AddAisleUiState()
    object Loading : AddAisleUiState()
    data class Success(val aisle: Aisle) : AddAisleUiState()
    sealed class Error : AddAisleUiState() {
        data class NoAccount(val message: String = "No account found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}