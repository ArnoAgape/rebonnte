package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for the aisle home screen.
 *
 * Handles aisle listing, search, selection mode,
 * and user actions related to an aisle.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeAisleViewModel @Inject constructor(
    private val aisleRepository: AisleRepository,
    userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _selection = MutableStateFlow(SelectionState())

    private val _isRefreshing = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)

    private val aislesFlow: Flow<List<Aisle>> =
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query ->
        aisleRepository.getAllAisles(query)
    }

    private val _uiState: Flow<HomeAisleUiState> =
        aislesFlow
            .map { aisles ->
                if (aisles.isEmpty()) {
                    HomeAisleUiState.Error.Empty()
                } else {
                    HomeAisleUiState.Success(aisles)
                }
            }
            .catch { e ->
                emit(HomeAisleUiState.Error.Generic(e.message ?: "Unknown error"))
            }

    private val _isSignedIn =
        userRepository.isUserSignedIn()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    private val _searchState: Flow<SearchState> =
        combine(_isSearchActive, _searchQuery) { active, query ->
            SearchState(active, query)
        }

    val screenState: StateFlow<AisleHomeScreenState> =
        combine(
            _uiState,
            _isRefreshing,
            _selection,
            _isSignedIn,
            _searchState
        ) { ui, refreshing, selection, signedIn, search ->

            AisleHomeScreenState(
                uiState = ui,
                isRefreshing = refreshing,
                selection = selection,
                isSignedIn = signedIn,
                isSearchActive = search.isActive,
                searchQuery = search.query
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

    fun activateSearch() {
        _isSearchActive.value = true
    }

    fun deactivateSearch() {
        _isSearchActive.value = false
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshAisles() {
        viewModelScope.launch {
            if (!networkUtils.isNetworkAvailable()) {
                _events.trySend(Event.ShowMessage(R.string.no_network))
                return@launch
            }
        }
    }

}

data class AisleHomeScreenState(
    val uiState: HomeAisleUiState = HomeAisleUiState.Loading,
    val isRefreshing: Boolean = false,
    val selection: SelectionState = SelectionState(),
    val isSignedIn: Boolean? = null,
    val isSearchActive: Boolean = false,
    val searchQuery: String = ""
)

data class SearchState(
    val isActive: Boolean,
    val query: String
)