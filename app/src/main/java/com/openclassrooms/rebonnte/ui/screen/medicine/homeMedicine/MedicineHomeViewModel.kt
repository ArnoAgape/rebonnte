package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.MedicineOrderField
import com.openclassrooms.rebonnte.domain.model.MedicineSort
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineHomeViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _sort = MutableStateFlow(MedicineSort.NAME_ASC)

    fun onSortSelected(sort: MedicineSort) {
        _sort.value = sort
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _medicines: StateFlow<List<Medicine>> =
        _sort.flatMapLatest { sort ->
            val (field, direction) = sort.toFirestoreOrder()
            medicineRepository.getMedicinesOrderBy(field, direction)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _selection = MutableStateFlow(SelectionState())
    private val _isRefreshing = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)

    private val filteredMedicinesFlow = combine(
        _medicines,
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

    val isSignedIn =
        userRepository.isUserSignedIn()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    private val _uiState: Flow<MedicineHomeUiState> =
        filteredMedicinesFlow
            .map { medicines ->
                if (medicines.isEmpty())
                    MedicineHomeUiState.Error.Empty()
                else
                    MedicineHomeUiState.Success(medicines)
            }
            .catch { e ->
                emit(MedicineHomeUiState.Error.Generic(e.message ?: "Unknown error"))
            }

    val screenState: StateFlow<MedicineHomeScreenState> =
        combine(
            _uiState,
            _isRefreshing,
            _selection,
            _isSearchActive,
            _searchQuery
        ) { ui, refreshing, selection, searchActive, searchQuery ->
            MedicineHomeScreenState(
                uiState = ui,
                isRefreshing = refreshing,
                selection = selection,
                isSearchActive = searchActive,
                searchQuery = searchQuery
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MedicineHomeScreenState()
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

    fun refreshMedicines() {
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

    fun MedicineSort.toFirestoreOrder(): Pair<MedicineOrderField, Query.Direction> =
        when (this) {
            MedicineSort.NAME_ASC ->
                MedicineOrderField.NAME to Query.Direction.ASCENDING

            MedicineSort.NAME_DESC ->
                MedicineOrderField.NAME to Query.Direction.DESCENDING

            MedicineSort.STOCK_ASC ->
                MedicineOrderField.STOCK to Query.Direction.ASCENDING

            MedicineSort.STOCK_DESC ->
                MedicineOrderField.STOCK to Query.Direction.DESCENDING

            MedicineSort.DATE_NEWEST ->
                MedicineOrderField.CREATED_AT to Query.Direction.DESCENDING

            MedicineSort.DATE_OLDEST ->
                MedicineOrderField.CREATED_AT to Query.Direction.ASCENDING
        }

}

data class MedicineHomeScreenState(
    val uiState: MedicineHomeUiState = MedicineHomeUiState.Loading,
    val isRefreshing: Boolean = false,
    val selection: SelectionState = SelectionState(),
    val isSearchActive: Boolean = false,
    val searchQuery: String = ""
)