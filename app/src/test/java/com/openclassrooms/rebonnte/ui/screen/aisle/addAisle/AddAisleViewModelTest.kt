package com.openclassrooms.rebonnte.ui.screen.aisle.addAisle

import com.openclassrooms.rebonnte.R
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.Event
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddAisleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val aisleRepo: AisleRepository = mockk()
    private val userRepo: UserRepository = mockk()
    private lateinit var viewModel: AddAisleViewModel

    @Before
    fun setup() {

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser(id = "1")

        viewModel = AddAisleViewModel(
            aisleRepository = aisleRepo,
            userRepository = userRepo
        )
    }

    @Test
    fun `addAisle emits success event`() = runTest {
        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")
        coEvery { aisleRepo.addAisle(any()) } returns Unit

        viewModel.onNameChanged("Paracetamol")

        viewModel.eventsFlow.test {
            viewModel.addAisle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowSuccessMessage::class.java)

            val successEvent = event as Event.ShowSuccessMessage
            assertThat(successEvent.message).isEqualTo(R.string.success_add_aisle)
        }
        coVerify(exactly = 1) {
            aisleRepo.addAisle(any())
        }
    }

    @Test
    fun `addAisle emits error event when aisle name is blank`() = runTest {
        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser("1")
        coEvery { aisleRepo.addAisle(any()) } returns Unit

        viewModel.onNameChanged("")

        viewModel.eventsFlow.test {
            viewModel.addAisle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.ShowMessage::class.java)

            val errorEvent = event as Event.ShowMessage
            assertThat(errorEvent.message).isEqualTo(R.string.error_invalid_form_aisle)
        }
        coVerify(exactly = 0) {
            aisleRepo.addAisle(any())
        }
    }
}