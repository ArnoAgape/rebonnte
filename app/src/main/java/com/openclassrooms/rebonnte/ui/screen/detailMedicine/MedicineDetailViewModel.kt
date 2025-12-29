package com.openclassrooms.rebonnte.ui.screen.detailMedicine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val medicineName: String =
        savedStateHandle["medicineName"]
            ?: error("medicineName missing")

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    val uiState: StateFlow<MedicineDetailUiState> =
        medicineRepository.medicines
            .map { medicines ->
                val filtered = medicines.filter { it.name == medicineName }

                when {
                    filtered.isEmpty() ->
                        MedicineDetailUiState.Error.Empty()

                    else ->
                        MedicineDetailUiState.Success(filtered)
                }
            }
            .catch { e ->
                emit(
                    MedicineDetailUiState.Error.Generic(
                        e.message ?: "Unknown error"
                    )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                MedicineDetailUiState.Loading
            )

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