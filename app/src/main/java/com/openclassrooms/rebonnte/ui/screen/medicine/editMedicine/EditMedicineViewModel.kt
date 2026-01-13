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
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.StockChangeType
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class EditMedicineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val medicineRepository: MedicineRepository,
    private val historyRepository: HistoryRepository,
    aisleRepository: AisleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val medicineId: String = checkNotNull(savedStateHandle["medicineId"])

    private var originalMedicine: Medicine? = null

    private val _uiState = MutableStateFlow<EditMedicineUiState>(EditMedicineUiState.Idle)
    val uiState: StateFlow<EditMedicineUiState> = _uiState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)

    private val _events = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = _events.receiveAsFlow()

    private val _medicine = MutableStateFlow(Medicine())
    val medicine: StateFlow<Medicine> = _medicine.asStateFlow()

    val aisles: StateFlow<List<Aisle>> =
        aisleRepository.aisles.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedAisle = MutableStateFlow<Aisle?>(null)
    val selectedAisle = _selectedAisle.asStateFlow()

    /**
     * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
     */
    val isMedicineValid = medicine
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
            isMedicineValid
        ) { ui, m, valid ->
            EditScreenState(
                uiState = ui,
                medicine = m,
                isValid = valid,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EditScreenState()
        )

    init {
        getCurrentMedicine()
        getCurrentAisle()
        getCurrentUser()
    }

    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.NameChanged -> {
                _medicine.update { it.copy(name = formEvent.name) }
            }

            is FormEvent.StockSet -> {
                _medicine.update { it.copy(stock = formEvent.stock) }
            }

            is FormEvent.DateTimeChanged -> {
                _medicine.update { it.copy(dateTime = formEvent.dateTime) }
            }

            is FormEvent.AisleSelected -> {
                _selectedAisle.value = formEvent.aisle
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

    fun editMedicine() {
        viewModelScope.launch {

            // 1. Check user logged in
            val currentUser = _user.value
            if (currentUser == null) {
                _uiState.value = EditMedicineUiState.Error.NoAccount()
                _events.trySend(Event.ShowMessage(R.string.error_no_account_edit_medicine))
                return@launch
            }

            // 2. Validate form
            if (!isMedicineValid.value) {
                _events.trySend(Event.ShowMessage(R.string.error_invalid_form_medicine))
                return@launch
            }

            _uiState.value = EditMedicineUiState.Loading

            // 3. Prepare objects
            val oldMedicine = originalMedicine
            if (oldMedicine == null) {
                _events.trySend(Event.ShowMessage(R.string.error_generic))
                return@launch
            }
            val medicineToSave = _medicine.value.copy(author = currentUser)

            // 4. Delta of stock
            val delta = medicineToSave.stock - oldMedicine.stock

            // 5. Saving medicine
            val resultMedicine = medicineRepository.editMedicine(medicineToSave)

            if (resultMedicine.isFailure) {
                val exception = resultMedicine.exceptionOrNull()
                _uiState.value = EditMedicineUiState.Error.Generic("Network error: ${exception?.message}")
                _events.trySend(Event.ShowMessage(R.string.error_generic))
                return@launch
            }

            // 6. History generated only if stock updated
            if (delta != 0) {
                val history = buildStockHistory(
                    medicine = medicineToSave,
                    delta = delta,
                    author = currentUser
                )

                historyRepository.addHistory(medicineId, history)
            }

            // 7. Final success
            _uiState.value = EditMedicineUiState.Success(medicineToSave)
            _events.trySend(Event.ShowSuccessMessage(R.string.success_edit_medicine))
        }
    }

    fun buildStockHistory(
        medicine: Medicine,
        delta: Int,
        author: User
    ): History {
        return History(
            medicineName = medicine.name,
            author = author,
            dateTime = Instant.now(),
            quantity = kotlin.math.abs(delta),
            changeType = if (delta > 0)
                StockChangeType.ADDED
            else
                StockChangeType.REMOVED
        )
    }

    fun getCurrentMedicine() {
        viewModelScope.launch {
            medicineRepository.getMedicineById(medicineId).collect { m ->
                if (m != null) {
                    if (originalMedicine == null) {
                        originalMedicine = m
                    }
                    _medicine.value = m
                }
            }
        }
    }

    fun getCurrentAisle() {
        viewModelScope.launch {
            aisles.collect { list ->
                val med = _medicine.value
                if (med.aisleId.isNotBlank()) {
                    _selectedAisle.value = list.firstOrNull { it.id == med.aisleId }
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }
}

data class EditScreenState(
    val uiState: EditMedicineUiState = EditMedicineUiState.Idle,
    val aisle: Aisle = Aisle(),
    val medicine: Medicine = Medicine(),
    val isValid: Boolean = false
)