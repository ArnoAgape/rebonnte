package com.openclassrooms.rebonnte.ui.screen.login

import com.openclassrooms.rebonnte.R
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.common.Event
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val userRepo: UserRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser(id = "1")
        coEvery { userRepo.isUserSignedIn() } returns flowOf(true)

        viewModel = LoginViewModel(userRepository = userRepo)
    }

    @Test
    fun `isSignedIn emits true when repository emits true`() = runTest {
        viewModel.isSignedIn.test {
            assertThat(awaitItem()).isEqualTo(true)
        }
    }

    @Test
    fun `sendEvent emits event in eventsFlow`() = runTest {

        val event = Event.ShowMessage(R.string.error_generic)

        viewModel.eventsFlow.test {
            viewModel.sendEvent(event)
            assertThat(awaitItem()).isEqualTo(event)
        }
    }

}