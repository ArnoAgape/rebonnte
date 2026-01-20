package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddMedicineViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val aisleRepo: AisleRepository = mockk()
    private val userRepo: UserRepository = mockk()
    private val medicineRepo: MedicineRepository = mockk()
    private lateinit var viewModel: AddMedicineViewModel

    @Before
    fun setup() {

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser(id = "1")
        coEvery { aisleRepo.getAllAisles() } returns flowOf(TestUtils.fakeAisles("1"))
        coEvery { medicineRepo.addMedicine(any()) } returns Result.success(Unit)

        viewModel = AddMedicineViewModel(
            medicineRepository = medicineRepo,
            aisleRepository = aisleRepo,
            userRepository = userRepo
        )
    }

    @Test
    fun `addMedicine emits success event`() = runTest {
        val aisle = TestUtils.fakeAisle("1")
        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")

        viewModel.onAction(FormEvent.NameChanged("Paracetamol"))
        viewModel.onAction(FormEvent.AisleSelected(aisle))

        viewModel.eventsFlow.test {
            viewModel.addMedicine()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowSuccessMessage::class.java)

            val successEvent = event as Event.ShowSuccessMessage
            assertThat(successEvent.message).isEqualTo(R.string.success_add_medicine)
        }
    }

    @Test
    fun `addMedicine emits error event when medicine name is blank`() = runTest {
        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")

        viewModel.onAction(FormEvent.NameChanged(""))

        viewModel.eventsFlow.test {
            viewModel.addMedicine()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowMessage::class.java)

            val errorEvent = event as Event.ShowMessage
            assertThat(errorEvent.message).isEqualTo(R.string.error_invalid_form_medicine)
        }
    }

    @Test
    fun `addMedicine emits no account event when user is null`() = runTest {
        coEvery { userRepo.getCurrentUser() } returns null

        viewModel.eventsFlow.test {
            viewModel.addMedicine()

            val event = awaitItem() as Event.ShowMessage
            assertThat(event.message).isEqualTo(R.string.error_no_account_add_medicine)
        }
    }

    @Test
    fun `isValid is true when medicine name is not blank`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()
            assertThat(initial.isValid).isFalse()

            viewModel.onAction(FormEvent.NameChanged("Paracetamol"))

            val intermediate = awaitItem()
            assertThat(intermediate.isValid).isFalse()

            val updated = awaitItem()
            assertThat(updated.isValid).isTrue()
        }
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