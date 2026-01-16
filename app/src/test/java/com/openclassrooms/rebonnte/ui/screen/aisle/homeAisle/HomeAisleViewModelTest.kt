package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.DetailAisleUiState
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.DetailAisleViewModel
import com.openclassrooms.rebonnte.ui.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeAisleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var aisleRepo: AisleRepository

    private lateinit var userRepo: UserRepository
    private lateinit var networkUtils: NetworkUtils


    @Before
    fun setup() {
        aisleRepo = mockk()
        userRepo = mockk()
        networkUtils = mockk()
    }

    private fun createViewModel(): HomeAisleViewModel {
        every { aisleRepo.getAisles() } returns flowOf(emptyList())
        every { networkUtils.isNetworkAvailable() } returns true
        every { userRepo.isUserSignedIn() } returns flowOf(true)

        return HomeAisleViewModel(
            aisleRepository = aisleRepo,
            userRepository = userRepo,
            networkUtils = networkUtils
        )
    }

    @Test
    fun `when aisles list is empty uiState is Error_Empty`() = runTest {
        val viewModel = createViewModel()

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.uiState).isInstanceOf(HomeAisleUiState.Error.Empty::class.java)
        }
    }

    @Test
    fun `when aisles list is not empty uiState is Success`() = runTest {

        every { aisleRepo.getAisles() } returns flowOf(TestUtils.fakeAisles(id = "123"))
        every { networkUtils.isNetworkAvailable() } returns true
        every { userRepo.isUserSignedIn() } returns flowOf(true)

        val viewModel = HomeAisleViewModel(
            aisleRepository = aisleRepo,
            userRepository = userRepo,
            networkUtils = networkUtils
        )

        viewModel.screenState.test {
            val state = awaitItem()
            assertThat(state.uiState).isInstanceOf(HomeAisleUiState.Success::class.java)
        }
    }
}