package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailAisleViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    aisleRepository: AisleRepository,
    savedStateHandle: SavedStateHandle,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val aisleId: String =
        checkNotNull(savedStateHandle["aisleId"])

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _selection = MutableStateFlow(SelectionState())
    private val _isRefreshing = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)

    private val _filteredMedicinesFlow: Flow<List<Medicine>> =
        combine(
            medicineRepository.getMedicinesByAisle(aisleId),
            _searchQuery
        ) { medicines, query ->
            if (query.isBlank()) {
                medicines
            } else {
                medicines.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }
        }

    private val _uiState: StateFlow<DetailAisleUiState> =
        combine(
            aisleRepository.getAisleById(aisleId),
            _filteredMedicinesFlow
        ) { aisle, medicines ->
            if (medicines.isEmpty()) {
                DetailAisleUiState.Error.Empty()
            } else {
                DetailAisleUiState.Success(
                    aisle = aisle,
                    medicines = medicines
                )
            }
        }
            .catch { e ->
                emit(DetailAisleUiState.Error.Generic(e.message ?: "Unknown error"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                DetailAisleUiState.Loading
            )

    val screenState: StateFlow<AisleDetailScreenState> =
        combine(
            _uiState,
            _isRefreshing,
            _selection,
            _isSearchActive,
            _searchQuery
        ) { ui, refreshing, selection, searchActive, searchQuery ->
            AisleDetailScreenState(
                uiState = ui,
                isRefreshing = refreshing,
                selection = selection,
                isSearchActive = searchActive,
                searchQuery = searchQuery
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AisleDetailScreenState()
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

    fun deleteSelectedMedicines() {
        viewModelScope.launch {
            val result = medicineRepository.deleteMedicines(_selection.value.selectedIds)

            if (result.isSuccess) {
                exitSelectionMode()
                _events.trySend(Event.ShowSuccessMessage(R.string.success_deleted_medicines))
            } else {
                _events.trySend(Event.ShowMessage(R.string.error_delete_medicine))
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

    fun refreshAisle() {
        viewModelScope.launch {
            if (!networkUtils.isNetworkAvailable()) {
                _events.trySend(Event.ShowMessage(R.string.no_network))
                return@launch
            }
        }
    }
}

data class AisleDetailScreenState(
    val uiState: DetailAisleUiState = DetailAisleUiState.Loading,
    val isRefreshing: Boolean = false,
    val selection: SelectionState = SelectionState(),
    val isSearchActive: Boolean = false,
    val searchQuery: String = ""
)