package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AisleDetailViewModel @Inject constructor(
    medicineRepository: MedicineRepository,
    aisleRepository: AisleRepository,
    savedStateHandle: SavedStateHandle,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _events = Channel<Event>()
    val eventsFlow = _events.receiveAsFlow()

    private val aisleId: String =
        checkNotNull(savedStateHandle["aisleId"])

    val uiState: StateFlow<AisleDetailUiState> =
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
                SharingStarted.WhileSubscribed(5_000),
                AisleDetailUiState.Loading
            )

    fun refreshAisle() {
        viewModelScope.launch {
            if (!networkUtils.isNetworkAvailable()) {
                _events.trySend(Event.ShowMessage(R.string.no_network))
                return@launch
            }
        }
    }
}