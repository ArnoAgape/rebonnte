package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.AisleDetailUiState
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class MedicineHomeViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>()
    val eventsFlow = _events.receiveAsFlow()

    private val _selection = MutableStateFlow(SelectionState())
    private val _isRefreshing = MutableStateFlow(false)

    val isSignedIn =
        userRepository.isUserSignedIn()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    private val _uiState =
        medicineRepository.medicines
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
            _selection
        ) { ui, refreshing, selection ->
            MedicineHomeScreenState(
                uiState = ui,
                isRefreshing = refreshing,
                selection = selection
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
}

data class MedicineHomeScreenState(
    val uiState: MedicineHomeUiState = MedicineHomeUiState.Loading,
    val isRefreshing: Boolean = false,
    val selection: SelectionState = SelectionState(),
)