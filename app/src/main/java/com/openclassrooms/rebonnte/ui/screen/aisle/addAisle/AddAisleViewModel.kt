package com.openclassrooms.rebonnte.ui.screen.aisle.addAisle

import android.R.attr.name
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class AddAisleViewModel @Inject constructor(
    private val aisleRepository: AisleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAisleUiState>(AddAisleUiState.Idle)
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _aisle = MutableStateFlow(Aisle())

    /**
     * StateFlow derived from the aisle that emits a FormError if the name is empty, null otherwise.
     */
    private val isAisleValid: StateFlow<Boolean> =
        _aisle
            .map { it.name.isNotBlank() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val state: StateFlow<AddAisleScreenState> =
        combine(
            _uiState,
            _aisle,
            isAisleValid
        ) { ui, a, valid ->
            AddAisleScreenState(
                uiState = ui,
                aisle = a,
                isValid = valid,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddAisleScreenState()
        )

    fun onNameChanged(name: String) {
        _aisle.update { it.copy(name = name) }
    }

    fun addAisle() {
        viewModelScope.launch {

            val user = userRepository.getCurrentUser()
            if (user == null) {
                _events.trySend(Event.ShowMessage(R.string.error_no_account_profile))
                return@launch
            }

            val name = _aisle.value.name.trim()
            if (name.isBlank()) {
                _events.trySend(Event.ShowMessage(R.string.error_invalid_form_aisle))
                return@launch
            }

            val aisleToSave = _aisle.value.copy(name = _aisle.value.name.trim())

            aisleRepository.addAisle(aisleToSave)

            _uiState.value = AddAisleUiState.Success(aisleToSave)
            _events.trySend(Event.ShowSuccessMessage(R.string.success_add_aisle))
        }
    }
}

data class AddAisleScreenState(
    val uiState: AddAisleUiState = AddAisleUiState.Idle,
    val aisle: Aisle = Aisle(),
    val isValid: Boolean = false
)