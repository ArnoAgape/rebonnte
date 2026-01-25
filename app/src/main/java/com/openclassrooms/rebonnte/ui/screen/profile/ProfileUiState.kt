package com.openclassrooms.rebonnte.ui.screen.profile

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.User

sealed class ProfileUiState {

    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()

    sealed class Error : ProfileUiState() {
        data class NoAccount(@param:StringRes val messageRes: Int = R.string.error_no_account_found) : Error()
        data class Generic(@param:StringRes val messageRes: Int = R.string.error_generic) : Error()
    }
}