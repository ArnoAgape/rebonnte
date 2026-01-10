package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class MedicineDetailUiState {
    object Idle : MedicineDetailUiState()
    object Loading : MedicineDetailUiState()
    object Deleted : MedicineDetailUiState()
    data class Success(val medicine: Medicine) : MedicineDetailUiState()
    sealed class Error : MedicineDetailUiState() {
        data class Empty(val message: String = "No medicines found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}