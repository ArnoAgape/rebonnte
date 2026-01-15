package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class HomeMedicineUiState {
    object Loading : HomeMedicineUiState()
    data class Success(val medicines: List<Medicine>) : HomeMedicineUiState()
    sealed class Error : HomeMedicineUiState() {
        data class Empty(val message: String = "No medicine found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}