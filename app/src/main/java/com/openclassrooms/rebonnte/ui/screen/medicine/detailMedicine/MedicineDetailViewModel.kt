package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    private val userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val medicineId: String = checkNotNull(savedStateHandle["medicineId"])

    private val medicineName: String =
        savedStateHandle["medicineName"]
            ?: error("medicineName missing")

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        DetailUiState(
            MedicineDetailUiState.Loading,
            HistoryUiState.Loading
        )
    )
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMedicine() {
        viewModelScope.launch {
            userRepository.observeCurrentUser()
                .filterNotNull()
                .flatMapLatest {
                    medicineRepository.getMedicineById(medicineId)
                }
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
                        MedicineDetailUiState.Error.Empty("Impossible to find the post")
                    }
                    _uiState.update { it.copy(medicineState = newState) }
                }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            if (!networkUtils.isNetworkAvailable()) {
                _events.trySend(Event.ShowMessage(R.string.no_network))
                return@launch
            }
            observeMedicine()
            observeHistory(medicineId)
        }
    }

    fun incrementStock(name: String) {
        if (!networkUtils.isNetworkAvailable()) {
            _events.trySend(Event.ShowMessage(R.string.no_network))
            return
        }
        //medicineRepository.incrementStock(medicineName)
    }

    fun decrementStock(name: String) {
        if (!networkUtils.isNetworkAvailable()) {
            _events.trySend(Event.ShowMessage(R.string.no_network))
            return
        }
        //medicineRepository.decrementStock(medicineName)
    }
}

data class DetailUiState(
    val medicineState: MedicineDetailUiState,
    val historyState: HistoryUiState
)