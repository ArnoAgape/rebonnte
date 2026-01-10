package com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class EditMedicineUiState {
    object Idle : EditMedicineUiState()
    object Loading : EditMedicineUiState()
    data class Success(val medicine: Medicine) : EditMedicineUiState()
    sealed class Error : EditMedicineUiState() {
        data class NoAccount(val message: String = "No account found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}