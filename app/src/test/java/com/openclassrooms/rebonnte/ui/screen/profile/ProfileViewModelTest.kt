package com.openclassrooms.rebonnte.ui.screen.profile

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.FormEvent
import com.openclassrooms.rebonnte.ui.utils.AndroidEmailValidator
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProfileViewModelTest {

    private val userRepo: UserRepository = mockk()
    private val networkUtils: NetworkUtils = mockk(relaxed = true)
    private val emailValidator: AndroidEmailValidator = mockk()

    private fun createViewModel(): ProfileViewModel {
        every { userRepo.observeCurrentUser() } returns flowOf(TestUtils.fakeUser(id = "1"))

        every { emailValidator.validate(any()) } returns true
        coEvery { userRepo.updateUser(any()) } returns Result.success(Unit)
        coEvery { userRepo.signOut() } returns Result.success(Unit)
        coEvery { userRepo.deleteUser() } returns Result.success(Unit)

        return ProfileViewModel(
            userRepository = userRepo,
            networkUtils = networkUtils,
            emailValidator = emailValidator
        )
    }

    @Test
    fun `saveUser updates profile name in state`() = runTest {
        val viewModel = createViewModel()

        // attendre que l'utilisateur soit chargé
        viewModel.state
            .filter { it.user != null }
            .first()

        // modifier le nom
        viewModel.onAction(FormEvent.DisplayNameChanged("New name"))

        viewModel.saveUser()

        val successState =
            viewModel.state
                .filter { it.uiState is ProfileUiState.Success }
                .first()

        val user =
            (successState.uiState as ProfileUiState.Success).user

        assertThat(user.displayName).isEqualTo("New name")
    }

    @Test
    fun `isValid is false when display name is blank`() = runTest {
        val viewModel = createViewModel()

        viewModel.state
            .filter { it.user != null }
            .first()

        viewModel.onAction(FormEvent.DisplayNameChanged(""))

        val state =
            viewModel.state
                .filter { !it.isValid }
                .first()

        assertThat(state.isValid).isFalse()
    }

    @Test
    fun `DisplayNameChanged updates user display name`() = runTest {
        val viewModel = createViewModel()

        viewModel.state
            .filter { it.user != null }
            .first()

        viewModel.onAction(FormEvent.DisplayNameChanged("John Doe"))

        val state =
            viewModel.state
                .filter { it.user?.displayName == "John Doe" }
                .first()

        assertThat(state.user?.displayName).isEqualTo("John Doe")
    }

    @Test
    fun `EmailChanged updates user email`() = runTest {
        val viewModel = createViewModel()

        viewModel.state
            .filter { it.user != null }
            .first()

        viewModel.onAction(FormEvent.EmailChanged("new@email.com"))

        val state =
            viewModel.state
                .filter { it.user?.email == "new@email.com" }
                .first()

        assertThat(state.user?.email).isEqualTo("new@email.com")
    }
}