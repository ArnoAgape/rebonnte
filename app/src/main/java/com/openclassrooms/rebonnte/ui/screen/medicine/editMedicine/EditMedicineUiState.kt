package com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class EditMedicineUiState {
    object Idle : EditMedicineUiState()
    object Loading : EditMedicineUiState()
    data class Success(val medicine: Medicine) : EditMedicineUiState()
    sealed class Error : EditMedicineUiState() {
        data class NoAccount(@param:StringRes val messageRes: Int = R.string.error_no_account_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}