package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine

sealed class DetailAisleUiState {
    object Loading : DetailAisleUiState()
    data class Success(val aisle: Aisle, val medicines: List<Medicine>) : DetailAisleUiState()

    sealed class Error : DetailAisleUiState() {
        data class Empty(@param:StringRes val messageRes: Int = R.string.error_aisle_not_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
        data class AisleDeleted(@param:StringRes val messageRes: Int = R.string.aisle_deleted) : Error()
    }
}