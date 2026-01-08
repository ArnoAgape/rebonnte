package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class MedicineHomeUiState {
    object Loading : MedicineHomeUiState()
    data class Success(val medicines: List<Medicine>) : MedicineHomeUiState()
    sealed class Error : MedicineHomeUiState() {
        data class Empty(val message: String = "No medicines found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}