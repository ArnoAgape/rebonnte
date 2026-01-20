package com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditMedicineViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val aisleRepo: AisleRepository = mockk()
    private val medicineRepo: MedicineRepository = mockk()
    private val historyRepo: HistoryRepository = mockk()
    private val userRepo: UserRepository = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: EditMedicineViewModel

    @Before
    fun setup() {

        savedStateHandle = SavedStateHandle().apply {
            set("medicineId", "123")
        }

        every { aisleRepo.getAisleById(any()) } returns flowOf(TestUtils.fakeAisle("123"))
        every { aisleRepo.getAllAisles() } returns flowOf(TestUtils.fakeAisles("123"))
        every { medicineRepo.getMedicinesByAisle(any(), any()) } returns flowOf(emptyList())
        every { medicineRepo.getMedicineById(any()) } returns flowOf(TestUtils.fakeMedicine("123"))
        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")
        coEvery { historyRepo.addStockHistory(any(), any(), any()) } just Runs


        viewModel = EditMedicineViewModel(
            medicineRepository = medicineRepo,
            savedStateHandle = savedStateHandle,
            historyRepository = historyRepo,
            userRepository = userRepo,
            aisleRepository = aisleRepo
        )
    }

    @Test
    fun `editMedicine emits success event`() = runTest {
        val aisle = TestUtils.fakeAisle("1")
        val medicine = TestUtils.fakeMedicine("1")

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")
        coEvery { medicineRepo.editMedicine(any()) } returns Result.success(Unit)

        viewModel.state
            .filter { it.medicine.id.isNotBlank() }
            .first()

        viewModel.onAction(FormEvent.NameChanged(medicine.name))
        viewModel.onAction(FormEvent.AisleSelected(aisle))
        viewModel.onAction(FormEvent.StockSet(medicine.stock))

        viewModel.eventsFlow.test {
            viewModel.editMedicine()

            val event = awaitItem() as Event.ShowSuccessMessage
            assertThat(event.message).isEqualTo(R.string.success_edit_medicine)
        }
    }

    @Test
    fun `editMedicine emits error event when medicine name is blank`() = runTest {
        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")

        viewModel.onAction(FormEvent.NameChanged(""))

        viewModel.eventsFlow.test {
            viewModel.editMedicine()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowMessage::class.java)

            val errorEvent = event as Event.ShowMessage
            assertThat(errorEvent.message).isEqualTo(R.string.error_invalid_form_medicine)
        }
    }

    @Test
    fun `editMedicine creates history when stock increases`() = runTest {
        val medicine = TestUtils.fakeMedicine("1")

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")
        coEvery { medicineRepo.editMedicine(any()) } returns Result.success(Unit)
        coEvery { historyRepo.addStockHistory(any(), any(), any()) } just Runs

        viewModel.state
            .filter { it.medicine.id.isNotBlank() }
            .first()

        viewModel.onAction(FormEvent.StockSet(medicine.stock + 1))

        viewModel.editMedicine()

        coVerify {
            historyRepo.addStockHistory(
                medicine = any(),
                delta = 1,
                author = any()
            )
        }
    }

    @Test
    fun `editMedicine creates history when stock decreases`() = runTest {
        val medicine = TestUtils.fakeMedicine("1")

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")
        coEvery { medicineRepo.editMedicine(any()) } returns Result.success(Unit)
        coEvery { historyRepo.addStockHistory(any(), any(), any()) } just Runs

        viewModel.state
            .filter { it.medicine.id.isNotBlank() }
            .first()

        viewModel.onAction(FormEvent.StockSet(medicine.stock - 1))

        viewModel.editMedicine()

        coVerify {
            historyRepo.addStockHistory(
                medicine = any(),
                delta = -1,
                author = any()
            )
        }
    }

    @Test
    fun `isValid becomes true when medicine name is not blank`() = runTest {
            viewModel.onAction(FormEvent.NameChanged("Paracetamol"))

            val state =
                viewModel.state
                    .filter { it.isValid }
                    .first()

            assertThat(state.isValid).isTrue()
    }

    @Test
    fun `AisleSelected updates medicine aisle fields`() = runTest {
        val aisle = TestUtils.fakeAisle("42")

        viewModel.onAction(FormEvent.AisleSelected(aisle))

        val state = viewModel.state
            .filter { it.medicine.aisleId.isNotBlank() }
            .first()

        assertThat(state.medicine.aisleId).isEqualTo("42")
        assertThat(state.medicine.aisleName).isEqualTo(aisle.name)
    }

    @Test
    fun `StockSet updates medicine stock`() = runTest {
        val medicine = TestUtils.fakeMedicine("1")
        viewModel.onAction(FormEvent.StockSet(medicine.stock))

        val state = viewModel.state
            .first()

        assertThat(state.medicine.stock).isEqualTo(10)
    }
}