package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class AddMedicineUiState {
    object Idle : AddMedicineUiState()
    object Loading : AddMedicineUiState()
    data class Success(val medicine: Medicine) : AddMedicineUiState()
    sealed class Error : AddMedicineUiState() {
        data class NoAccount(@param:StringRes val messageRes: Int = R.string.error_no_account_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}