package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.History

sealed class DetailHistoryUiState {
    object Loading : DetailHistoryUiState()
    data class Success(val history: List<History>) : DetailHistoryUiState()
    sealed class Error : DetailHistoryUiState() {
        data class Empty(@param:StringRes val messageRes: Int = R.string.no_history) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}