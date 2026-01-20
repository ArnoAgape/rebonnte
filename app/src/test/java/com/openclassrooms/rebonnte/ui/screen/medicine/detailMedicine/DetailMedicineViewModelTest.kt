package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.R
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailMedicineViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val aisleRepo: AisleRepository = mockk()
    private val medicineRepo: MedicineRepository = mockk()
    private val historyRepo: HistoryRepository = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private val networkUtils: NetworkUtils = mockk()

    @Before
    fun setup() {

        savedStateHandle = SavedStateHandle().apply {
            set("medicineId", "123")
        }
    }

    private fun createViewModel(): DetailMedicineViewModel {
        every { aisleRepo.getAisleById(any()) } returns flowOf(TestUtils.fakeAisle("123"))
        every { medicineRepo.getMedicinesByAisle(any(), any()) } returns flowOf(emptyList())
        every { medicineRepo.getMedicineById(any()) } returns flowOf(TestUtils.fakeMedicine("123"))

        return DetailMedicineViewModel(
            medicineRepository = medicineRepo,
            savedStateHandle = savedStateHandle,
            networkUtils = networkUtils,
            historyRepository = historyRepo
        )
    }

    @Test
    fun testNameMedicine() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())
        val viewModel = createViewModel()

        val successState =
            viewModel.screenState
                .filter { it.medicineState is DetailMedicineUiState.Success }
                .first()

        val medicine =
            (successState.medicineState as DetailMedicineUiState.Success).medicine

        assertThat(medicine.name).isEqualTo("Paracetamol")
    }

    @Test
    fun testNameAisle() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())
        val viewModel = createViewModel()

        val successState =
            viewModel.screenState
                .filter { it.medicineState is DetailMedicineUiState.Success }
                .first()

        val medicine =
            (successState.medicineState as DetailMedicineUiState.Success).medicine

        assertThat(medicine.aisleName).isEqualTo("Painkiller")
    }

    @Test
    fun testStock() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())
        val viewModel = createViewModel()

        val successState =
            viewModel.screenState
                .filter { it.medicineState is DetailMedicineUiState.Success }
                .first()

        val medicine =
            (successState.medicineState as DetailMedicineUiState.Success).medicine

        assertThat(medicine.stock).isEqualTo(10)
    }

    @Test
    fun testAddMedicineWithSpecificUser() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())
        val viewModel = createViewModel()

        val successState =
            viewModel.screenState
                .filter { it.medicineState is DetailMedicineUiState.Success }
                .first()

        val medicine =
            (successState.medicineState as DetailMedicineUiState.Success).medicine

        assertThat(medicine.author?.displayName).isEqualTo("John Doe")
    }

    @Test
    fun `historyState is Empty when no history`() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())

        val viewModel = createViewModel()

        val state =
            viewModel.screenState
                .filter { it.historyState !is DetailHistoryUiState.Loading }
                .first()

        assertThat(state.historyState)
            .isInstanceOf(DetailHistoryUiState.Error.Empty::class.java)
    }

    @Test
    fun `historyState is Success when history exists`() = runTest {
        every { historyRepo.observeHistory(any()) } returns
                flowOf(TestUtils.fakeHistories("1"))

        val viewModel = createViewModel()

        val state =
            viewModel.screenState
                .filter { it.historyState is DetailHistoryUiState.Success }
                .first()

        val history =
            (state.historyState as DetailHistoryUiState.Success).history

        assertThat(history).isNotEmpty()
    }

    @Test
    fun `deleteMedicine emits success event when deletion succeeds`() = runTest {
        every { medicineRepo.getMedicineById(any()) } returns flowOf(TestUtils.fakeMedicine("123"))
        coEvery { medicineRepo.deleteMedicines(any()) } returns Result.success(Unit)
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())

        val viewModel = createViewModel()

        viewModel.screenState
            .filter { it.medicineState is DetailMedicineUiState.Success }
            .first()

        viewModel.eventsFlow.test {
            viewModel.deleteMedicine()

            val event = awaitItem() as Event.ShowSuccessMessage
            assertThat(event.message)
                .isEqualTo(R.string.success_deleted_medicine)
        }
    }

    @Test
    fun `refreshData emits no network event when offline`() = runTest {
        every { networkUtils.isNetworkAvailable() } returns false
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())

        val viewModel = createViewModel()

        viewModel.eventsFlow.test {
            viewModel.refreshData()

            val offlineEvent = awaitItem() as Event.ShowMessage
            assertThat(offlineEvent.message).isEqualTo(R.string.no_network)
        }
    }
}