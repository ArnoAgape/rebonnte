package com.openclassrooms.rebonnte.ui.screen.medicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class MedicineUiState {
    object Idle : MedicineUiState()
    object Loading : MedicineUiState()
    data class Success(val medicines: List<Medicine>) : MedicineUiState()
    sealed class Error : MedicineUiState() {
        data class Empty(val message: String = "No medicines found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}