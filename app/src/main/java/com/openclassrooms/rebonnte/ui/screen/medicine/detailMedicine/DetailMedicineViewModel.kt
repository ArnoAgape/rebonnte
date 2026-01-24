package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for the medicine detail screen.
 *
 * Handles medicine retrieval, history observation,
 * deletion actions, and UI state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailMedicineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    private val aisleRepository: AisleRepository,
    historyRepository: HistoryRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val medicineId: String = checkNotNull(savedStateHandle["medicineId"])
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _isRefreshing = MutableStateFlow(false)

    private val medicineStateFlow: StateFlow<DetailMedicineUiState> =
        medicineRepository.getMedicineById(medicineId)
            .map { medicine ->
                if (medicine != null) {
                    DetailMedicineUiState.Success(medicine)
                } else {
                    DetailMedicineUiState.Error.Empty("Medicine not found")
                }
            }
            .onStart { emit(DetailMedicineUiState.Loading) }
            .catch { e ->
                emit(
                    DetailMedicineUiState.Error.Generic(
                        e.message ?: "Unknown error"
                    )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DetailMedicineUiState.Loading
            )

    private val aisleStateFlow: StateFlow<Boolean> =
        medicineRepository.getMedicineById(medicineId)
            .map { medicine ->
                medicine?.aisleId
            }
            .flatMapLatest { aisleId ->
                if (aisleId.isNullOrBlank()) {
                    flowOf(true)
                } else {
                    aisleRepository.getAisleById(aisleId)
                        .map { false }
                        .catch { emit(true) }
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )


    private val historyStateFlow: StateFlow<DetailHistoryUiState> =
        historyRepository.observeHistory(medicineId)
            .map { history ->
                if (history.isNotEmpty()) {
                    DetailHistoryUiState.Success(history)
                } else {
                    DetailHistoryUiState.Error.Empty("No history found")
                }
            }
            .onStart { emit(DetailHistoryUiState.Loading) }
            .catch { e ->
                emit(
                    DetailHistoryUiState.Error.Generic(
                        e.message ?: "Unknown error"
                    )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DetailHistoryUiState.Loading
            )

    val screenState: StateFlow<MedicineDetailScreenState> =
        combine(
            medicineStateFlow,
            historyStateFlow,
            _isRefreshing,
            aisleStateFlow
        ) { medicineState, historyState, refreshing, isAisleDeleted ->
            MedicineDetailScreenState(
                medicineState = medicineState,
                historyState = historyState,
                isRefreshing = refreshing,
                isAisleDeleted = isAisleDeleted
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MedicineDetailScreenState()
        )

    fun deleteMedicine() = viewModelScope.launch {
        val medicine =
            (screenState.value.medicineState as? DetailMedicineUiState.Success)?.medicine

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
        viewModelScope.launch {
            if (!networkUtils.isNetworkAvailable()) {
                _events.trySend(Event.ShowMessage(R.string.no_network))
                return@launch
            }
        }
    }

}

data class MedicineDetailScreenState(
    val medicineState: DetailMedicineUiState = DetailMedicineUiState.Loading,
    val historyState: DetailHistoryUiState = DetailHistoryUiState.Loading,
    val isRefreshing: Boolean = false,
    val isAisleDeleted: Boolean = false
)