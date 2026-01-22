package com.openclassrooms.rebonnte.ui.screen.profile

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.FormEvent
import com.openclassrooms.rebonnte.ui.utils.AndroidEmailValidator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val userRepo: UserRepository = mockk()
    private val emailValidator: AndroidEmailValidator = mockk(relaxed = true)

    private fun createViewModel(): ProfileViewModel {
        every { userRepo.observeCurrentUser() } returns flowOf(TestUtils.fakeUser(id = "1"))

        coEvery { userRepo.updateUser(any()) } returns Result.success(Unit)
        coEvery { userRepo.signOut() } returns Result.success(Unit)
        coEvery { userRepo.deleteUser() } returns Result.success(Unit)

        return ProfileViewModel(
            userRepository = userRepo,
            emailValidator = emailValidator
        )
    }

    @Test
    fun `saveUser updates profile name in state`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FormEvent.DisplayNameChanged("New name"))

        viewModel.state.test {

            viewModel.saveUser()

            val idleState = awaitItem()
            assertThat(idleState.uiState).isEqualTo(ProfileUiState.Idle)

            var state = awaitItem()
            while (state.uiState !is ProfileUiState.Success) {
                state = awaitItem()
            }

            assertThat(state.user?.displayName).isEqualTo("New name")
        }
    }

    @Test
    fun `isValid is false when display name is blank`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FormEvent.DisplayNameChanged(""))

        viewModel.state.test {
            viewModel.saveUser()

            val idleState = awaitItem()
            assertThat(idleState.uiState).isEqualTo(ProfileUiState.Idle)

            var state = awaitItem()
            while (state.uiState !is ProfileUiState.Success) {
                state = awaitItem()
            }

            assertThat(state.isValid).isFalse()
        }
    }

    @Test
    fun `DisplayName and Email updated`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FormEvent.DisplayNameChanged("John Doe"))
        viewModel.onAction(FormEvent.EmailChanged("new@mail.fr"))

        viewModel.state.test {
            viewModel.saveUser()

            val idleState = awaitItem()
            assertThat(idleState.uiState).isEqualTo(ProfileUiState.Idle)

            var state = awaitItem()
            while (state.uiState !is ProfileUiState.Success) {
                state = awaitItem()
            }

            assertThat(state.user?.displayName).isEqualTo("John Doe")
            assertThat(state.user?.email).isEqualTo("new@mail.fr")
        }
    }

    @Test
    fun `emailValidator emits error when email is not valid`() = runTest {
        val viewModel = createViewModel()

        every { emailValidator.validate("new@") } returns false

        viewModel.state.test {
            viewModel.saveUser()

            val idleState = awaitItem()
            assertThat(idleState.uiState).isEqualTo(ProfileUiState.Idle)

            var state = awaitItem()
            while (state.uiState !is ProfileUiState.Success) {
                state = awaitItem()
            }

            assertThat(state.isValid).isFalse()
        }
    }

    @Test
    fun `saveUser emits generic error when repository fails`() = runTest {
        val viewModel = createViewModel()

        coEvery { userRepo.updateUser(any()) } throws Exception("Boom")

        viewModel.state.test {
            viewModel.saveUser()

            var state = awaitItem()
            while (state.uiState !is ProfileUiState.Error.Generic) {
                state = awaitItem()
            }

            assertThat(state.uiState).isInstanceOf(ProfileUiState.Error.Generic::class.java)

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.eventsFlow.test {
            val event = awaitItem() as Event.ShowMessage
            assertThat(event.message).isEqualTo(R.string.error_generic)

            cancelAndIgnoreRemainingEvents()
        }
    }

}