package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineHomeUiState
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AisleDetailViewModel @Inject constructor(
    medicineRepository: MedicineRepository,
    aisleRepository: AisleRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>()
    val eventsFlow = _events.receiveAsFlow()

    val uiState =
        medicineRepository.medicines
            .map { medicines ->
                if (medicines.isEmpty()) AisleDetailUiState.Error.Empty()
                else AisleDetailUiState.Success(medicines)
            }
            .catch { e ->
                emit(AisleDetailUiState.Error.Generic(e.message ?: "Unknown error"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AisleDetailUiState.Loading
            )

    fun refreshMedicines() {
        if (!networkUtils.isNetworkAvailable()) {
            _events.trySend(Event.ShowMessage(R.string.no_network))
        }
    }
}