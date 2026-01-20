package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    aisleRepository: AisleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val uiState = MutableStateFlow<AddMedicineUiState>(AddMedicineUiState.Idle)
    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val aisles: StateFlow<List<Aisle>> =
        aisleRepository.getAllAisles()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    private val selectedAisle = MutableStateFlow<Aisle?>(null)

    private val _medicine = MutableStateFlow(Medicine())

    /**
     * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
     */
    private val isMedicineValid: StateFlow<Boolean> =
        _medicine
        .map { it.name.isNotBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val state: StateFlow<AddScreenState> =
        combine(
            uiState,
            _medicine,
            isMedicineValid,
            aisles,
            selectedAisle
        ) { ui, m, valid, a, s ->
            AddScreenState(
                uiState = ui,
                medicine = m,
                isValid = valid,
                aisles = a,
                selectedAisle = s
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AddScreenState()
        )

    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.NameChanged -> {
                _medicine.update { it.copy(name = formEvent.name.trim()) }
            }

            is FormEvent.StockSet -> {
                _medicine.update { it.copy(stock = formEvent.stock) }
            }

            is FormEvent.DateTimeChanged -> {
                _medicine.update { it.copy(dateTime = formEvent.dateTime) }
            }

            is FormEvent.AisleSelected -> {
                selectedAisle.value = formEvent.aisle
                _medicine.update {
                    it.copy(
                        aisleId = formEvent.aisle.id,
                        aisleName = formEvent.aisle.name
                    )
                }
            }

            else -> {}
        }
    }

    fun addMedicine() {
        viewModelScope.launch {

            val user = userRepository.getCurrentUser()
            if (user == null) {
                _events.trySend(Event.ShowMessage(R.string.error_no_account_add_medicine))
                return@launch
            }

            val name = _medicine.value.name.trim()
            if (name.isBlank()) {
                _events.trySend(Event.ShowMessage(R.string.error_invalid_form_medicine))
                return@launch
            }

            val medicineToSave = _medicine.value.copy(name = name)

            uiState.value = AddMedicineUiState.Loading
            medicineRepository.addMedicine(medicineToSave)

            uiState.value = AddMedicineUiState.Success(medicineToSave)
            _events.trySend(Event.ShowSuccessMessage(R.string.success_add_medicine))

        }
    }
}

data class AddScreenState(
    val uiState: AddMedicineUiState = AddMedicineUiState.Idle,
    val medicine: Medicine = Medicine(),
    val isValid: Boolean = false,
    val aisles: List<Aisle> = emptyList(),
    val selectedAisle: Aisle? = null
)