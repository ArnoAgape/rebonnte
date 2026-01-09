package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.compose.runtime.IntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    private val historyRepository: HistoryRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val medicineId: String = checkNotNull(savedStateHandle["medicineId"])
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        DetailUiState(
            MedicineDetailUiState.Loading,
            HistoryUiState.Loading
        )
    )
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _stock = mutableIntStateOf(0)
    val stock: IntState = _stock

    fun initStock(medicine: Medicine) {
        _stock.intValue = medicine.stock
    }

    fun onStockChange(newValue: Int) {
        _stock.intValue = newValue.coerceAtLeast(0)
    }

    init {
        observeMedicine()
        observeHistory(medicineId)
    }

    private fun observeMedicine() {
        viewModelScope.launch {
            medicineRepository.getMedicineById(medicineId)
                .onStart {
                    _uiState.update {
                        it.copy(medicineState = MedicineDetailUiState.Loading)
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            medicineState = MedicineDetailUiState.Error.Generic(
                                e.message ?: "Unknown error"
                            )
                        )
                    }
                }
                .collect { medicine ->
                    val newState = if (medicine != null) {
                        MedicineDetailUiState.Success(medicine)
                    } else {
                        MedicineDetailUiState.Error.Empty("Impossible to find the medicine")
                    }
                    _uiState.update { it.copy(medicineState = newState) }
                }
        }
    }

    private fun observeHistory(medicineId: String) {
        viewModelScope.launch {
            historyRepository.observeHistory(medicineId)
                .onStart {
                    _uiState.update {
                        it.copy(historyState = HistoryUiState.Loading)
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            historyState = HistoryUiState.Error.Generic(
                                e.message ?: "Unknown error"
                            )
                        )
                    }
                }
                .collect { history ->
                    val newState = if (history.isNotEmpty()) {
                        HistoryUiState.Success(history)
                    } else {
                        HistoryUiState.Error.Empty("No history found")
                    }
                    _uiState.update { it.copy(historyState = newState) }
                }
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

data class DetailUiState(
    val medicineState: MedicineDetailUiState,
    val historyState: HistoryUiState
)