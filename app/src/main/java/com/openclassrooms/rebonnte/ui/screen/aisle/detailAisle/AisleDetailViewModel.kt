package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
class AisleDetailViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    aisleRepository: AisleRepository,
    savedStateHandle: SavedStateHandle,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>()
    val eventsFlow = _events.receiveAsFlow()

    private val _selection = MutableStateFlow(SelectionState())

    private val _isRefreshing = MutableStateFlow(false)

    private val aisleId: String =
        checkNotNull(savedStateHandle["aisleId"])

    private val _uiState: StateFlow<AisleDetailUiState> =
        combine(
            aisleRepository.getAisleById(aisleId),
            medicineRepository.getMedicinesByAisle(aisleId)
        ) { aisle, medicines ->
            if (medicines.isEmpty()) {
                AisleDetailUiState.Error.Empty()
            } else {
                AisleDetailUiState.Success(
                    aisle = aisle,
                    medicines = medicines
                )
            }
        }
            .catch { e ->
                emit(AisleDetailUiState.Error.Generic(e.message ?: "Unknown error"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AisleDetailUiState.Loading
            )

    val screenState: StateFlow<AisleDetailScreenState> =
        combine(
            _uiState,
            _isRefreshing,
            _selection
        ) { ui, refreshing, selection ->
            AisleDetailScreenState(
                uiState = ui,
                isRefreshing = refreshing,
                selection = selection
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
    val uiState: AisleDetailUiState = AisleDetailUiState.Loading,
    val isRefreshing: Boolean = false,
    val selection: SelectionState = SelectionState(),
)