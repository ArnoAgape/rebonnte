package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
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
    aisleRepository: AisleRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

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

    val state: StateFlow<AisleHomeScreenState> =
        combine(_uiState, _isRefreshing) { ui, refreshing ->
            AisleHomeScreenState(
                uiState = ui,
                isRefreshing = refreshing
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AisleHomeScreenState()
        )

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
 * including loading state and refresh status.
 */
data class AisleHomeScreenState(
    val uiState: AisleHomeUiState = AisleHomeUiState.Loading,
    val isRefreshing: Boolean = false
)