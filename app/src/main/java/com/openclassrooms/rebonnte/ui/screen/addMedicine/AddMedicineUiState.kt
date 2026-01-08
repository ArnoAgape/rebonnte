package com.openclassrooms.rebonnte.ui.screen.addMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class AddMedicineUiState {
    object Idle : AddMedicineUiState()
    object Loading : AddMedicineUiState()
    data class Success(val medicine: Medicine) : AddMedicineUiState()
    sealed class Error : AddMedicineUiState() {
        data class NoAccount(val message: String = "No account found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}