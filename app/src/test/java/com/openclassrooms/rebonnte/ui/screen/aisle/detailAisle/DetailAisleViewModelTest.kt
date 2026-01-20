package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.jvm.java

class DetailAisleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val aisleRepo: AisleRepository = mockk()
    private val medicineRepo: MedicineRepository = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private val networkUtils: NetworkUtils = mockk()


    @Before
    fun setup() {

        savedStateHandle = SavedStateHandle().apply {
            set("aisleId", "123")
        }
    }

    private fun createViewModel(): DetailAisleViewModel {
        every { aisleRepo.getAisleById(any()) } returns flowOf(TestUtils.fakeAisle("123"))
        every { medicineRepo.getMedicinesByAisle(any(), any()) } returns flowOf(emptyList())
        every { networkUtils.isNetworkAvailable() } returns true

        return DetailAisleViewModel(
            medicineRepository = medicineRepo,
            aisleRepository = aisleRepo,
            savedStateHandle = savedStateHandle,
            networkUtils = networkUtils
        )
    }

    @Test
    fun `when medicines list is empty uiState is Error_Empty`() = runTest {
        val viewModel = createViewModel()

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.uiState).isEqualTo(DetailAisleUiState.Error.Empty())
        }
    }

    @Test
    fun `when medicines list is not empty uiState is Success`() = runTest {
        val aisle = TestUtils.fakeAisle(id = "123")
        val medicines = listOf(TestUtils.fakeMedicine("1"), TestUtils.fakeMedicine("2"))

        every { aisleRepo.getAisleById("123") } returns flowOf(aisle)
        every { medicineRepo.getMedicinesByAisle("123", any()) } returns flowOf(medicines)
        every { networkUtils.isNetworkAvailable() } returns true

        val viewModel = DetailAisleViewModel(
            medicineRepository = medicineRepo,
            aisleRepository = aisleRepo,
            savedStateHandle = savedStateHandle,
            networkUtils = networkUtils
        )

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.uiState).isEqualTo(DetailAisleUiState.Success(aisle, medicines))
        }
    }

    @Test
    fun `enterSelectionMode sets isSelectionMode to true`() = runTest {
        val viewModel = createViewModel()

        viewModel.enterSelectionMode()

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.selection.isSelectionMode).isTrue()
        } 
    }

    @Test
    fun `exitSelectionMode resets selection state`() = runTest {
        val viewModel = createViewModel()

        viewModel.enterSelectionMode()
        viewModel.toggleSelection("1")
        viewModel.exitSelectionMode()

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.selection.isSelectionMode).isFalse()
            assertThat(state.selection.selectedIds).isEmpty()
        }
    }

    @Test
    fun `toggleSelection adds and removes id`() = runTest {
        val viewModel = createViewModel()

        viewModel.enterSelectionMode()
        viewModel.toggleSelection("1")

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.selection.selectedIds).contains("1")
        }

        viewModel.toggleSelection("1")

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.selection.selectedIds).doesNotContain("1")
        }
    }

    @Test
    fun `deleteSelectedMedicines success resets selection and emits success event`() = runTest {
        val viewModel = createViewModel()

        coEvery { medicineRepo.deleteMedicines(any()) } returns Result.success(Unit)

        viewModel.enterSelectionMode()
        viewModel.toggleSelection("1")

        viewModel.eventsFlow.test {
            viewModel.deleteSelectedMedicines()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowSuccessMessage::class.java)

            val successEvent = event as Event.ShowSuccessMessage
            assertThat(successEvent.message).isEqualTo(R.string.success_deleted_medicines)
        }

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.selection.isSelectionMode).isFalse()
            assertThat(state.selection.selectedIds).isEmpty()
        }
    }

    @Test
    fun `activateSearch sets isSearchActive to true`() = runTest {
        val viewModel = createViewModel()

        viewModel.activateSearch()

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.isSearchActive).isTrue()
        }
    }

    @Test
    fun `deactivateSearch disables search and clears query`() = runTest {
        val viewModel = createViewModel()

        viewModel.activateSearch()
        viewModel.updateSearchQuery("para")
        viewModel.deactivateSearch()

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.isSearchActive).isFalse()
            assertThat(state.searchQuery).isEmpty()
        }
    }

    @Test
    fun `updateSearchQuery updates searchQuery`() = runTest {
        val viewModel = createViewModel()

        viewModel.updateSearchQuery("para")

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.searchQuery).isEqualTo("para")
        }
    }

    @Test
    fun `refreshAisle emits no network message when offline`() = runTest {
        val viewModel = createViewModel()

        every { networkUtils.isNetworkAvailable() } returns false

        viewModel.eventsFlow.test {
            viewModel.refreshAisle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowMessage::class.java)

            val offlineEvent = event as Event.ShowMessage
            assertThat(offlineEvent.message).isEqualTo(R.string.no_network)
        }
    }

}