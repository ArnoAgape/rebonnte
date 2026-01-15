package com.openclassrooms.rebonnte.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openclassrooms.rebonnte.ui.screen.aisle.addAisle.AddAisleScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.addAisle.AddAisleViewModel
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.AddMedicineScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.AddMedicineViewModel
import com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle.HomeAisleScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle.HomeAisleViewModel
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.DetailAisleScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.DetailAisleViewModel
import com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine.DetailMedicineScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine.DetailMedicineViewModel
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.screen.login.authSignInLauncher
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.HomeMedicineScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.HomeMedicineViewModel
import com.openclassrooms.rebonnte.ui.screen.login.LoginScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine.EditMedicineScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.editMedicine.EditMedicineViewModel
import com.openclassrooms.rebonnte.ui.screen.profile.ProfileScreen
import com.openclassrooms.rebonnte.ui.screen.profile.ProfileViewModel

/**
 * Defines the navigation graph for the application.
 * Each destination binds its screen with the appropriate ViewModel.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    // Sign-in launchers
    val isSignedIn by loginViewModel.isSignedIn.collectAsStateWithLifecycle()

    val emailSignUpLauncher = authSignInLauncher(loginViewModel)

    NavHost(
        navController = navController,
        startDestination = Login
    ) {

        composable<Login> {
            LoginScreen(
                onLaunchAuth = emailSignUpLauncher,
                isSignedIn = isSignedIn,
                onLoginSuccess = {
                    navController.navigate(HomeAisle) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeAisle> {
            HomeAisleScreen(
                viewModel = hiltViewModel<HomeAisleViewModel>(),
                onFABClick = { navController.navigate(AddAisle) },
                onAisleClick = { aisle -> navController.navigate(DetailAisle(aisle.id)) }
            )
        }

        composable<HomeMedicine> {
            HomeMedicineScreen(
                viewModel = hiltViewModel<HomeMedicineViewModel>(),
                onFABClick = { navController.navigate(AddMedicine) },
                onMedicineClick = { medicine -> navController.navigate(DetailMedicine(medicine.id)) }
            )
        }

        composable<DetailAisle> {
            DetailAisleScreen(
                viewModel = hiltViewModel<DetailAisleViewModel>(),
                onMedicineClick = { medicine ->
                    navController.navigate(
                        DetailMedicine(medicine.id)
                    )
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable<DetailMedicine> {
            DetailMedicineScreen(
                viewModel = hiltViewModel<DetailMedicineViewModel>(),
                onBackClick = { navController.navigateUp() },
                onEditClick = { medicineId ->
                    navController.navigate(EditMedicine(medicineId))
                }
            )
        }

        composable<AddMedicine> {
            AddMedicineScreen(
                viewModel = hiltViewModel<AddMedicineViewModel>(),
                onBackClick = { navController.navigateUp() },
                onSaveClick = { navController.navigateUp() }
            )
        }

        composable<AddAisle> {
            AddAisleScreen(
                viewModel = hiltViewModel<AddAisleViewModel>(),
                onBackClick = { navController.navigateUp() },
                onSaveClick = { navController.navigateUp() }
            )
        }

        composable<EditMedicine> {
            EditMedicineScreen(
                viewModel = hiltViewModel<EditMedicineViewModel>(),
                onBackClick = { navController.navigateUp() },
                onSaveClick = { navController.navigateUp() }
            )
        }

        composable<Profile> {
            ProfileScreen(
                viewModel = hiltViewModel<ProfileViewModel>(),
                onLoginScreen = { navController.navigate(Login) }
            )
        }
    }
}