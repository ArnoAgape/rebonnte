package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.compose.runtime.IntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    historyRepository: HistoryRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val medicineId: String = checkNotNull(savedStateHandle["medicineId"])
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _stock = mutableIntStateOf(0)
    val stock: IntState = _stock

    private val _isRefreshing = MutableStateFlow(false)

    private val medicineStateFlow: Flow<MedicineDetailUiState> =
        medicineRepository.getMedicineById(medicineId)
            .map { medicine ->
                if (medicine != null) {
                    MedicineDetailUiState.Success(medicine)
                } else {
                    MedicineDetailUiState.Error.Empty("Medicine not found")
                }
            }
            .onStart { emit(MedicineDetailUiState.Loading) }
            .catch { e ->
                emit(
                    MedicineDetailUiState.Error.Generic(
                        e.message ?: "Unknown error"
                    )
                )
            }

    private val historyStateFlow: Flow<HistoryDetailUiState> =
        historyRepository.observeHistory(medicineId)
            .map { history ->
                if (history.isNotEmpty()) {
                    HistoryDetailUiState.Success(history)
                } else {
                    HistoryDetailUiState.Error.Empty("No history found")
                }
            }
            .onStart { emit(HistoryDetailUiState.Loading) }
            .catch { e ->
                emit(
                    HistoryDetailUiState.Error.Generic(
                        e.message ?: "Unknown error"
                    )
                )
            }

    val screenState: StateFlow<MedicineDetailScreenState> =
        combine(
            medicineStateFlow,
            historyStateFlow,
            _isRefreshing
        ) { medicineState, historyState, refreshing ->
            MedicineDetailScreenState(
                medicineState = medicineState,
                historyState = historyState,
                isRefreshing = refreshing
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MedicineDetailScreenState()
        )

    fun deleteMedicine() = viewModelScope.launch {
        val medicine =
            (screenState.value.medicineState as? MedicineDetailUiState.Success)?.medicine

        if (medicine == null) {
            _events.trySend(Event.ShowMessage(R.string.error_medicine_not_found))
            return@launch
        }

        val result = medicineRepository.deleteMedicines(setOf(medicine.id))

        if (result.isSuccess) {
            _events.trySend(Event.ShowSuccessMessage(R.string.success_deleted_medicine))
        } else {
            _events.trySend(Event.ShowMessage(R.string.error_delete_medicine))
        }
    }

    fun refreshData() {
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

data class MedicineDetailScreenState(
    val medicineState: MedicineDetailUiState = MedicineDetailUiState.Loading,
    val historyState: HistoryDetailUiState = HistoryDetailUiState.Loading,
    val isRefreshing: Boolean = false
)