package com.openclassrooms.rebonnte.ui.screen.profile

import com.openclassrooms.rebonnte.domain.model.User

/**
 * Represents the UI state for the profile screen.
 */
sealed class ProfileUiState {

    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    sealed class Error : ProfileUiState() {
        data class NoAccount(val message: String = "No account found") : Error()
        data class Generic(val message: String = "Unknown error") : Error()
    }
}