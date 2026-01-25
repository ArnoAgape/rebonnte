package com.openclassrooms.rebonnte.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import com.openclassrooms.rebonnte.ui.utils.AndroidEmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.text.isNotBlank
import kotlin.text.orEmpty

/**
 * ViewModel responsible for managing user profile data.
 *
 * It observes the authenticated user, validates input fields,
 * persists profile updates, emits UI events, and handles sign-out or deletion.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val emailValidator: AndroidEmailValidator
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    private val _user = MutableStateFlow<User?>(null)
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val isUserFieldsValid = _user
        .map { currentUser ->
            val displayName = currentUser?.displayName.orEmpty()
            displayName.isNotBlank() && emailValidator.validate(currentUser?.email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val state: StateFlow<ProfileScreenState> =
        combine(
            _uiState,
            _user,
            isUserFieldsValid
        ) { ui, u, valid ->
            ProfileScreenState(
                uiState = ui,
                user = u,
                isValid = valid
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileScreenState()
        )

    init {
        observeCurrentUser()
    }

    /**
     * Updates local user fields based on form input.
     */
    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.DisplayNameChanged -> {
                _user.update { it?.copy(displayName = formEvent.displayName) }
            }

            is FormEvent.EmailChanged -> {
                _user.update { it?.copy(email = formEvent.email) }
            }

            else -> Unit
        }
    }

    fun saveUser() {
        viewModelScope.launch {

            // 1. If user logged in checking
            val currentUser = _user.value
            if (currentUser == null) {
                _uiState.value = ProfileUiState.Error.NoAccount()
                _events.trySend(Event.ShowMessage(R.string.error_no_account_profile))
                return@launch
            }

            _uiState.value = ProfileUiState.Loading

            try {
                // 2. Creation of file with user
                val userToSave = _user.value?.copy(
                    displayName = currentUser.displayName,
                    email = currentUser.email
                )

                if (userToSave != null) {
                    // 3. Updates User on Firebase
                    userRepository.updateUser(userToSave)

                    // 4. Success UI
                    _uiState.value = ProfileUiState.Success(userToSave)
                    _events.trySend(Event.ShowSuccessMessage(R.string.success_user_updated))
                }

            } catch (_: IOException) {
                // 5. Network error (impossible upload)
                _uiState.value = ProfileUiState.Error.Generic()
                _events.trySend(Event.ShowMessage(R.string.no_network))

            } catch (_: Exception) {
                // 6. Generic error (Firebase Storage, Firestore, etc.)
                _uiState.value = ProfileUiState.Error.Generic()
                _events.trySend(Event.ShowMessage(R.string.error_generic))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val result = userRepository.signOut()
            if (result.isSuccess) {
                _user.value = null
                _events.trySend(Event.ShowSuccessMessage(R.string.success_sign_out))
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = userRepository.deleteUser()
            if (result.isSuccess) {
                _user.value = null
                _events.trySend(Event.ShowSuccessMessage(R.string.success_deleted_account))
            }
        }
    }

    fun observeCurrentUser() {
        viewModelScope.launch {
            userRepository.observeCurrentUser()
                .collect { user ->
                    _user.value = user
                }
        }
    }
}

data class ProfileScreenState(
    val uiState: ProfileUiState = ProfileUiState.Idle,
    val user: User? = User(),
    val isValid: Boolean = false
)