package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for loading and refreshing the list of files.
 *
 * It exposes UI state, manages refresh actions, and emits one-time
 * events such as network warnings.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AisleHomeViewModel @Inject constructor(
    private val aisleRepository: AisleRepository,
    userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _selection = MutableStateFlow(SelectionState())

    private val _isRefreshing = MutableStateFlow(false)

    private val _uiState =
        aisleRepository.aisles
            .map { aisles ->
                if (aisles.isEmpty())
                    AisleHomeUiState.Error.Empty()
                else
                    AisleHomeUiState.Success(aisles)
            }
            .catch { e ->
                emit(AisleHomeUiState.Error.Generic(e.message ?: "Unknown error"))
            }

    val isSignedIn =
        userRepository.isUserSignedIn()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    val screenState: StateFlow<AisleHomeScreenState> =
        combine(
            _uiState,
            _isRefreshing,
            _selection,
            isSignedIn
        ) { ui, refreshing, selection, signedIn ->
            AisleHomeScreenState(
                uiState = ui,
                isRefreshing = refreshing,
                selection = selection,
                isSignedIn = signedIn
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AisleHomeScreenState()
        )

    fun enterSelectionMode() {
        _selection.update { it.copy(isSelectionMode = true) }
    }

    fun exitSelectionMode() {
        _selection.value = SelectionState() // reset
    }

    fun toggleSelection(id: String) {
        _selection.update { sel ->
            val set = sel.selectedIds.toMutableSet()
            if (!set.add(id)) set.remove(id)
            sel.copy(selectedIds = set)
        }
    }

    fun deleteSelectedAisles() {
        viewModelScope.launch {
            val result = aisleRepository.deleteAisles(_selection.value.selectedIds)

            if (result.isSuccess) {
                exitSelectionMode()
                _events.trySend(Event.ShowSuccessMessage(R.string.success_deleted_medicines))
            } else {
                _events.trySend(Event.ShowMessage(R.string.error_delete_aisle))
            }
        }
    }

    fun refreshAisles() {
        if (!networkUtils.isNetworkAvailable()) {
            _events.trySend(Event.ShowMessage(R.string.no_network))
            return
        }

        viewModelScope.launch {
            _isRefreshing.value = true
            delay(700)
            _isRefreshing.value = false
        }
    }
}

/**
 * Combined UI state for the home screen,
 * including loading state, refresh status, selection state and if the user is signed in.
 */

data class AisleHomeScreenState(
    val uiState: AisleHomeUiState = AisleHomeUiState.Loading,
    val isRefreshing: Boolean = false,
    val selection: SelectionState = SelectionState(),
    val isSignedIn: Boolean? = null
)