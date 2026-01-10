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
import com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle.AisleHomeScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle.AisleHomeViewModel
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.AisleDetailViewModel
import com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine.MedicineDetailViewModel
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.screen.login.authSignInLauncher
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineHomeScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineHomeViewModel
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
            AisleHomeScreen(
                viewModel = hiltViewModel<AisleHomeViewModel>(),
                onFABClick = { navController.navigate(AddAisle) },
                onAisleClick = { aisle -> navController.navigate(DetailAisle(aisle.id)) }
            )
        }

        composable<HomeMedicine> {
            MedicineHomeScreen(
                viewModel = hiltViewModel<MedicineHomeViewModel>(),
                onFABClick = { navController.navigate(AddMedicine) },
                onMedicineClick = { medicine -> navController.navigate(DetailMedicine(medicine.id)) }
            )
        }

        composable<DetailAisle> {
            AisleDetailScreen(
                viewModel = hiltViewModel<AisleDetailViewModel>(),
                onMedicineClick = { medicine ->
                    navController.navigate(
                        DetailMedicine(medicine.id)
                    )
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable<DetailMedicine> {
            MedicineDetailScreen(
                viewModel = hiltViewModel<MedicineDetailViewModel>(),
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