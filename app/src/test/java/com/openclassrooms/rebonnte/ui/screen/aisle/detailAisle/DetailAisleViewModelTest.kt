package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import com.openclassrooms.rebonnte.MainDispatcherRule
import com.openclassrooms.rebonnte.TestUtils
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.ui.screen.aisle.addAisle.AddAisleViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule

class DetailAisleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var aisleRepo: AisleRepository
    private lateinit var userRepo: UserRepository
    private lateinit var viewModel: AddAisleViewModel

    @Before
    fun setup() {
        aisleRepo = mockk()
        userRepo = mockk()

        coEvery { userRepo.getCurrentUser() } returns TestUtils.fakeUser(id = "1")

        viewModel = AddAisleViewModel(
            aisleRepository = aisleRepo,
            userRepository = userRepo
        )
    }
}