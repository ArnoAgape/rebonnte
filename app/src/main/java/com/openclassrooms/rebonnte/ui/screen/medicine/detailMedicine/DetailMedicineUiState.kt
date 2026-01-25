package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.AddMedicineUiState.Error

sealed class DetailMedicineUiState {
    object Loading : DetailMedicineUiState()
    object Deleted : DetailMedicineUiState()
    data class Success(val medicine: Medicine) : DetailMedicineUiState()
    sealed class Error : DetailMedicineUiState() {
        data class Empty(@param:StringRes val messageRes: Int = R.string.error_medicine_not_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}