package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
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
    private val aisleRepository: AisleRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<AisleHomeUiState>(AisleHomeUiState.Loading)
    val uiState: StateFlow<AisleHomeUiState> = _uiState

    val state: StateFlow<AisleHomeScreenState> =
        combine(
            uiState,
            isRefreshing
        ) { ui, isRef ->
            AisleHomeScreenState(
                uiState = ui,
                isRefreshing = isRef
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AisleHomeScreenState(
                uiState = AisleHomeUiState.Loading,
                isRefreshing = false
            )
        )

    init {
        networkUtils.checkNetwork(networkUtils, _events)
    }

    private fun getAllAisles() {
        viewModelScope.launch {
            aisleRepository.aisles
                .onStart { _uiState.value = AisleHomeUiState.Loading }
                .catch { e ->
                    _uiState.value = AisleHomeUiState.Error.Generic(e.message ?: "Unknown error")
                }
                .collect { posts ->
                    _uiState.value = if (posts.isEmpty()) {
                        AisleHomeUiState.Error.Empty()
                    } else {
                        AisleHomeUiState.Success(posts)
                    }
                }
        }
    }

    fun refreshAisles() {
        viewModelScope.launch {
            networkUtils.checkNetwork(networkUtils, _events)
            _isRefreshing.value = true

            kotlinx.coroutines.delay(700)

            _isRefreshing.value = false
        }
    }
}

/**
 * Combined UI state for the home screen,
 * including loading state and refresh status.
 */
data class AisleHomeScreenState(
    val uiState: AisleHomeUiState = AisleHomeUiState.Idle,
    val isRefreshing: Boolean = false
)