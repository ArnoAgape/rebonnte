package com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.AddMedicineUiState
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
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
import java.io.IOException
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class EditMedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    aisleRepository: AisleRepository,
    private val userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddMedicineUiState>(AddMedicineUiState.Idle)
    val uiState: StateFlow<AddMedicineUiState> = _uiState.asStateFlow()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    private val _events = Channel<Event>()
    val eventsFlow = _events.receiveAsFlow()

    val aisles: StateFlow<List<Aisle>> =
        aisleRepository.aisles
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _selectedAisle = MutableStateFlow<Aisle?>(null)
    val selectedAisle = _selectedAisle.asStateFlow()

    private val _medicine = MutableStateFlow(
        Medicine(
            id = "",
            name = "",
            stock = 1,
            dateTime = Instant.now(),
            aisleName = "",
            histories = emptyList()
        )
    )

    /**
     * Public state flow representing the current post being edited.
     * This is immutable for consumers.
     */
    val medicine: StateFlow<Medicine> = _medicine.asStateFlow()

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

    val state: StateFlow<AddScreenState> =
        combine(
            uiState,
            medicine,
            isMedicineValid
        ) { ui, m, valid ->
            AddScreenState(
                uiState = ui,
                medicine = m,
                isValid = valid,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AddScreenState()
        )

    init {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
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

    fun addMedicine() {
        viewModelScope.launch {

            // 1. Network checking
            if (!networkUtils.isNetworkAvailable()) {
                _events.trySend(Event.ShowMessage(R.string.no_network))
                return@launch
            }

            // 2. If user logged in checking
            val currentUser = _user.value
            if (currentUser == null) {
                _uiState.value = AddMedicineUiState.Error.NoAccount()
                _events.trySend(Event.ShowMessage(R.string.error_no_account_medicine))
                return@launch
            }

            _uiState.value = AddMedicineUiState.Loading

            try {
                // 3. Creation of file with user
                val medicineToSave = _medicine.value.copy(author = currentUser)

                // 4. Upload Storage + Firestore
                if (!isMedicineValid.value) {
                    _events.trySend(Event.ShowMessage(R.string.error_invalid_form_medicine))
                    return@launch
                }

                medicineRepository.addMedicine(medicineToSave)

                // 5. Success UI
                _uiState.value = AddMedicineUiState.Success(medicineToSave)
                _events.trySend(Event.ShowSuccessMessage(R.string.success_add_medicine))

            } catch (e: IOException) {
                // 6. Network error (impossible upload)
                _uiState.value = AddMedicineUiState.Error.Generic("Network error: ${e.message}")
                _events.trySend(Event.ShowMessage(R.string.no_network))

            } catch (_: Exception) {
                // 7. Generic error (Firebase Storage, Firestore, etc.)
                _uiState.value = AddMedicineUiState.Error.Generic()
                _events.trySend(Event.ShowMessage(R.string.error_generic))
            }
        }
    }
}

data class AddScreenState(
    val uiState: AddMedicineUiState = AddMedicineUiState.Idle,
    val medicine: Medicine = Medicine(),
    val isValid: Boolean = false
)