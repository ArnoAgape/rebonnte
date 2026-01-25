package com.openclassrooms.rebonnte.ui.screen.aisle.addAisle

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle

sealed class AddAisleUiState {
    object Idle : AddAisleUiState()
    object Loading : AddAisleUiState()
    data class Success(val aisle: Aisle) : AddAisleUiState()
    sealed class Error : AddAisleUiState() {
        data class NoAccount(@param:StringRes val messageRes: Int = R.string.error_no_account_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}