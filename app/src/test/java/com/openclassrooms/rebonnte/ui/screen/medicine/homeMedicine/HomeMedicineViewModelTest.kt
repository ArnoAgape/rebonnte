package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

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
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
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

class HomeMedicineViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val userRepo: UserRepository = mockk()
    private val medicineRepo: MedicineRepository = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private val networkUtils: NetworkUtils = mockk()


    @Before
    fun setup() {

        savedStateHandle = SavedStateHandle().apply {
            set("aisleId", "123")
        }
    }

    private fun createViewModel(): HomeMedicineViewModel {
        every { medicineRepo.getAllMedicines(any(), any()) } returns flowOf(emptyList())
        every { networkUtils.isNetworkAvailable() } returns true
        every { userRepo.isUserSignedIn() } returns flowOf(true)

        return HomeMedicineViewModel(
            medicineRepository = medicineRepo,
            userRepository = userRepo,
            networkUtils = networkUtils
        )
    }

    @Test
    fun `when medicines list is empty uiState is Error_Empty`() = runTest {
        val viewModel = createViewModel()

        viewModel.screenState.test {
            val loading = awaitItem()
            assertThat(loading.uiState).isEqualTo(HomeMedicineUiState.Loading)
            val state = awaitItem()
            assertThat(state.uiState).isEqualTo(HomeMedicineUiState.Error.Empty())
        }
    }

    @Test
    fun `when medicines list is not empty uiState is Success`() = runTest {
        val medicines = TestUtils.fakeMedicines("1")

        every { medicineRepo.getAllMedicines(any(), any()) } returns flowOf(medicines)
        every { userRepo.isUserSignedIn() } returns flowOf(true)

        val viewModel = HomeMedicineViewModel(
            medicineRepository = medicineRepo,
            userRepository = userRepo,
            networkUtils = networkUtils
        )

        viewModel.screenState.test {
            val loading = awaitItem()
            assertThat(loading.uiState).isEqualTo(HomeMedicineUiState.Loading)
            val state = awaitItem()
            assertThat(state.uiState).isEqualTo(HomeMedicineUiState.Success(medicines))
        }
    }

    @Test
    fun `enterSelectionMode sets isSelectionMode to true`() = runTest {
        val viewModel = createViewModel()

        viewModel.enterSelectionMode()

        viewModel.screenState.test {
            awaitItem()
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
            awaitItem()
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
            awaitItem()
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
            awaitItem()
            val state = awaitItem()
            assertThat(state.searchQuery).isEqualTo("para")
        }
    }

    @Test
    fun `refreshAisle emits no network message when offline`() = runTest {
        val viewModel = createViewModel()

        every { networkUtils.isNetworkAvailable() } returns false

        viewModel.eventsFlow.test {
            viewModel.refreshMedicines()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowMessage::class.java)

            val offlineEvent = event as Event.ShowMessage
            assertThat(offlineEvent.message).isEqualTo(R.string.no_network)
        }
    }

}