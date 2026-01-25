package com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for editing a new medicine.
 *
 * Manages form state, validation, and user interactions
 * related to medicine edition.
 */
@HiltViewModel
class EditMedicineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository,
    aisleRepository: AisleRepository
) : ViewModel() {

    private val medicineId: String = checkNotNull(savedStateHandle["medicineId"])

    private var originalStock: Int? = null

    private val uiState = MutableStateFlow<EditMedicineUiState>(EditMedicineUiState.Idle)
    private val _user = MutableStateFlow<User?>(null)

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val medicine = MutableStateFlow(Medicine())

    private val medicineFlow =
        medicineRepository.getMedicineById(medicineId)
            .filterNotNull()

    private val aisles: StateFlow<List<Aisle>> =
        aisleRepository.getAllAisles().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedAisle: StateFlow<Aisle?> =
        combine(aisles, medicine) { aisleList, med ->
            aisleList.firstOrNull { it.id == med.aisleId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
     */
    private val isMedicineValid: StateFlow<Boolean> =
        medicine
            .map { it.name.isNotBlank() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val state: StateFlow<EditScreenState> =
        combine(
            uiState,
            medicine,
            isMedicineValid,
            aisles,
            selectedAisle
        ) { ui, m, valid, a, s ->
            EditScreenState(
                uiState = ui,
                medicine = m,
                isValid = valid,
                aisles = a,
                selectedAisle = s
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EditScreenState()
        )

    init {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()

            medicineFlow
                .collect { m ->
                    medicine.value = m
                    originalStock = m.stock
                }
        }
    }

    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.NameChanged -> {
                medicine.update { it.copy(name = formEvent.name.trim()) }
            }

            is FormEvent.StockSet -> {
                medicine.update { it.copy(stock = formEvent.stock) }
            }

            is FormEvent.DateTimeChanged -> {
                medicine.update { it.copy(dateTime = formEvent.dateTime) }
            }

            is FormEvent.AisleSelected -> {
                medicine.update {
                    it.copy(
                        aisleId = formEvent.aisle.id,
                        aisleName = formEvent.aisle.name
                    )
                }
            }

            else -> {}
        }
    }

    fun editMedicine() {
        viewModelScope.launch {

            // 1. Check user logged in
            val currentUser = userRepository.getCurrentUser()
            if (currentUser == null) {
                _events.trySend(Event.ShowMessage(R.string.error_no_account_add_medicine))
                return@launch
            }

            // 2. Validate form
            val name = medicine.value.name.trim()
            if (name.isBlank()) {
                _events.trySend(Event.ShowMessage(R.string.error_invalid_form_medicine))
                return@launch
            }

            // 3. Prepare objects
            val medicineToSave = medicine.value.copy(name = name, author = currentUser)
            val oldStock = originalStock ?: run {
                _events.trySend(Event.ShowMessage(R.string.error_generic))
                return@launch
            }

            uiState.value = EditMedicineUiState.Loading

            val newStock = medicineToSave.stock
            val delta = newStock - oldStock

            // 4. Saving medicine
            val resultMedicine = medicineRepository.editMedicine(medicineToSave)

            if (resultMedicine.isFailure) {
                uiState.value = EditMedicineUiState.Error.Generic()
                _events.trySend(Event.ShowMessage(R.string.error_generic))
                return@launch
            }

            // 5. History generated only if stock updated
            if (delta != 0) {
                historyRepository.addStockHistory(
                    medicine = medicineToSave,
                    delta = delta,
                    author = currentUser
                )
            }

            originalStock = newStock

            // 6. Success UI
            uiState.value = EditMedicineUiState.Success(medicineToSave)
            _events.trySend(Event.ShowSuccessMessage(R.string.success_edit_medicine))
        }
    }
}

data class EditScreenState(
    val uiState: EditMedicineUiState = EditMedicineUiState.Idle,
    val medicine: Medicine = Medicine(),
    val isValid: Boolean = false,
    val aisles: List<Aisle> = emptyList(),
    val selectedAisle: Aisle? = null
)