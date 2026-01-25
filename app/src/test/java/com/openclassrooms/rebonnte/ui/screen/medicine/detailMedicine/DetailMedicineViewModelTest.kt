package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import com.openclassrooms.rebonnte.R
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.service.medicine.MedicineApi
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
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
    private val medicineApi: MedicineApi = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private val networkUtils: NetworkUtils = mockk()

    @Before
    fun setup() {

        every { networkUtils.isNetworkAvailable() } returns true

        savedStateHandle = SavedStateHandle().apply {
            set("medicineId", "123")
        }
    }

    private fun createViewModel(): DetailMedicineViewModel {
        every { aisleRepo.getAisleById(any()) } returns flowOf(TestUtils.fakeAisle("123"))
        every { medicineRepo.getMedicinesByAisle(any(), any(), any()) } returns flowOf(emptyList())
        every { medicineRepo.getMedicineById(any()) } returns flowOf(TestUtils.fakeMedicine("123"))

        return DetailMedicineViewModel(
            medicineRepository = medicineRepo,
            savedStateHandle = savedStateHandle,
            networkUtils = networkUtils,
            historyRepository = historyRepo,
            aisleRepository = aisleRepo
        )
    }

    @Test
    fun `medicine success contains correct data`() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())
        val viewModel = createViewModel()

        viewModel.screenState.test {
            val success = awaitItem ()

            val medicine =
                (success.medicineState as DetailMedicineUiState.Success).medicine

            assertThat(medicine.name).isEqualTo("Paracetamol")
            assertThat(medicine.stock).isEqualTo(10)
            assertThat(medicine.aisleName).isEqualTo("Painkiller")
            assertThat(medicine.author?.displayName).isEqualTo("John Doe")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `historyState is Empty when no history`() = runTest {
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())

        val viewModel = createViewModel()

        viewModel.screenState.test {

            val emptyState = awaitItem()
            assertThat(emptyState.historyState)
                .isInstanceOf(DetailHistoryUiState.Error.Empty::class.java)

            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `historyState is Success when history exists`() = runTest {
        val histories = listOf(TestUtils.fakeHistory("1"))
        every { historyRepo.observeHistory(any()) } returns flowOf(histories)

        val viewModel = createViewModel()

        viewModel.screenState.test {

            val successState = awaitItem()
            assertThat(successState.historyState)
                .isInstanceOf(DetailHistoryUiState.Success::class.java)

            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `deleteMedicine emits success event when deletion succeeds`() = runTest {
        every { medicineRepo.getMedicineById(any()) } returns flowOf(TestUtils.fakeMedicine("123"))
        coEvery { medicineRepo.deleteMedicines(any()) } returns Result.success(Unit)
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())

        val viewModel = createViewModel()

        viewModel.screenState
            .filter { it.medicineState is DetailMedicineUiState.Success }
            .test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }

        viewModel.eventsFlow.test {
            viewModel.deleteMedicine()

            val event = awaitItem() as Event.ShowSuccessMessage
            assertThat(event.message)
                .isEqualTo(R.string.success_deleted_medicine)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteMedicine deletes history before deleting medicine`() = runTest {
        val medicineId = TestUtils.fakeMedicine("123").id

        coEvery { historyRepo.deleteHistory(medicineId) } returns Result.success(Unit)
        coEvery { medicineRepo.deleteMedicines(any()) } returns Result.success(Unit)
        every { historyRepo.observeHistory(any()) } returns flowOf(emptyList())

        val repository = MedicineRepository(
            medicineApi = medicineApi,
            historyRepository = historyRepo
        )

        repository.deleteMedicines(setOf(medicineId))

        coVerifyOrder {
            historyRepo.deleteHistory(medicineId)
            medicineApi.deleteMedicines(setOf(medicineId))
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

            cancelAndIgnoreRemainingEvents()
        }
    }
}