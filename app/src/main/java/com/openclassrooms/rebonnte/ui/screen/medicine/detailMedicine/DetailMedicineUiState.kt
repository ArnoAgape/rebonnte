package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class DetailMedicineUiState {
    object Loading : DetailMedicineUiState()
    object Deleted : DetailMedicineUiState()
    data class Success(val medicine: Medicine) : DetailMedicineUiState()
    sealed class Error : DetailMedicineUiState() {
        data class Empty(val message: String = "No medicines found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}