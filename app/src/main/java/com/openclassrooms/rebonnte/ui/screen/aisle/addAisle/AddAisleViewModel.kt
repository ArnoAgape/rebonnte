package com.openclassrooms.rebonnte.ui.screen.aisle.addAisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddAisleViewModel @Inject constructor(
    private val aisleRepository: AisleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAisleUiState>(AddAisleUiState.Idle)
    val uiState: StateFlow<AddAisleUiState> = _uiState.asStateFlow()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _aisle = MutableStateFlow(
        Aisle(
            id = "",
            name = "",
            medicines = emptyList()
        )
    )

    /**
     * Public state flow representing the current aisle being edited.
     * This is immutable for consumers.
     */
    val aisle: StateFlow<Aisle> = _aisle.asStateFlow()

    /**
     * StateFlow derived from the aisle that emits a FormError if the name is empty, null otherwise.
     */
    val isAisleValid = aisle
        .map { it.name.isNotBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val state: StateFlow<AddAisleScreenState> =
        combine(
            uiState,
            aisle,
            isAisleValid
        ) { ui, a, valid ->
            AddAisleScreenState(
                uiState = ui,
                aisle = a,
                isValid = valid,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AddAisleScreenState()
        )

    init {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }

    fun onNameChanged(name: String) {
        _aisle.update { it.copy(name = name) }
    }

    fun addAisle() {
        viewModelScope.launch {

            // 2. If user logged in checking
            val currentUser = _user.value
            if (currentUser == null) {
                _uiState.value = AddAisleUiState.Error.NoAccount()
                _events.trySend(Event.ShowMessage(R.string.error_no_account_profile))
                return@launch
            }

            _uiState.value = AddAisleUiState.Loading

            try {

                // 4. Upload Storage + Firestore
                if (!isAisleValid.value) {
                    _events.trySend(Event.ShowMessage(R.string.error_invalid_form_aisle))
                    return@launch
                }

                aisleRepository.addAisle(_aisle.value)

                // 5. Success UI
                _uiState.value = AddAisleUiState.Success(_aisle.value)
                _events.trySend(Event.ShowSuccessMessage(R.string.success_add_aisle))

            } catch (e: IOException) {
                // 6. Network error (impossible upload)
                _uiState.value = AddAisleUiState.Error.Generic("Network error: ${e.message}")
                _events.trySend(Event.ShowMessage(R.string.no_network))

            } catch (_: Exception) {
                // 7. Generic error (Firebase Storage, Firestore, etc.)
                _uiState.value = AddAisleUiState.Error.Generic()
                _events.trySend(Event.ShowMessage(R.string.error_generic))
            }
        }
    }
}

data class AddAisleScreenState(
    val uiState: AddAisleUiState = AddAisleUiState.Idle,
    val aisle: Aisle = Aisle(),
    val isValid: Boolean = false
)