package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineHomeViewModel @Inject constructor(
    medicineRepository: MedicineRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>()
    val eventsFlow = _events.receiveAsFlow()

    private val _isRefreshing = MutableStateFlow(false)

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

    val state: StateFlow<MedicineHomeScreenState> =
        combine(_uiState, _isRefreshing) { ui, refreshing ->
            MedicineHomeScreenState(
                uiState = ui,
                isRefreshing = refreshing
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MedicineHomeScreenState()
        )

    fun refreshMedicines() {
        if (!networkUtils.isNetworkAvailable()) {
            _events.trySend(Event.ShowMessage(R.string.no_network))
            return
        }

        viewModelScope.launch {
            _isRefreshing.value = true
            delay(700) // feedback UX
            _isRefreshing.value = false
        }
    }
}

data class MedicineHomeScreenState(
    val uiState: MedicineHomeUiState = MedicineHomeUiState.Loading,
    val isRefreshing: Boolean = false
)