package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle

sealed class HomeAisleUiState {
    object Loading : HomeAisleUiState()
    data class Success(val aisles: List<Aisle>) : HomeAisleUiState()
    sealed class Error : HomeAisleUiState() {
        data class Empty(@param:StringRes val messageRes: Int = R.string.error_aisle_not_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}