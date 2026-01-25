package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class HomeMedicineUiState {
    object Loading : HomeMedicineUiState()
    data class Success(val medicines: List<Medicine>) : HomeMedicineUiState()
    sealed class Error : HomeMedicineUiState() {
        data class Empty(@param:StringRes val messageRes: Int = R.string.error_medicine_not_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}