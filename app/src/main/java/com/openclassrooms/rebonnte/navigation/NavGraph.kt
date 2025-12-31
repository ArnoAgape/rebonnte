package com.openclassrooms.rebonnte.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openclassrooms.rebonnte.ui.screen.addMedicine.AddScreen
import com.openclassrooms.rebonnte.ui.screen.addMedicine.AddMedicineViewModel
import com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle.AisleScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle.AisleHomeViewModel
import com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine.MedicineDetailViewModel
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.screen.login.authSignInLauncher
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineHomeViewModel
import com.openclassrooms.rebonnte.ui.screen.login.LoginScreen

/**
 * Defines the navigation graph for the application.
 * Each destination binds its screen with the appropriate ViewModel.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val aisleHomeViewModel: AisleHomeViewModel = hiltViewModel()
    val medicineHomeViewModel: MedicineHomeViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()

    // Sign-in launchers
    val isSignedIn by loginViewModel.isSignedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isSignedIn) {
        if (isSignedIn == true) {
            navController.navigate(AisleRoute) {
                popUpTo(LoginRoute) { inclusive = true }
            }
        }
    }
    val emailSignUpLauncher = authSignInLauncher(loginViewModel)

    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {

        composable<LoginRoute> {
            LoginScreen(
                onLaunchAuth = emailSignUpLauncher,
                isSignedIn = isSignedIn
            )
        }

        composable<AisleRoute> {
            AisleScreen(
                viewModel = aisleHomeViewModel,
                onAisleClick = { aisle -> navController.navigate(DetailAisle(aisle.name)) }
            )
        }

        composable<MedicineRoute> {
            MedicineScreen(
                viewModel = medicineHomeViewModel,
                loginViewModel = loginViewModel,
                onFABClick = { navController.navigate(AddMedicineRoute) },
                onMedicineClick = { medicine -> navController.navigate(DetailMedicine(medicine.name)) }
            )
        }

        composable<DetailAisle> {
            AisleDetailScreen(
                viewModel = hiltViewModel<MedicineHomeViewModel>(),
                onMedicineClick = { medicine ->
                    navController.navigate(
                        DetailMedicine(medicine.name)
                    )
                }
            )
        }

        composable<AddMedicineRoute> {
            AddScreen(
                viewModel = hiltViewModel<AddMedicineViewModel>(),
                onBackClick = { navController.navigateUp() },
                onSaveClick = { navController.navigateUp() }
            )
        }

        composable<DetailMedicine> {
            MedicineDetailScreen(
                viewModel = hiltViewModel<MedicineDetailViewModel>(),
                medicineName = it.arguments?.getString("nameMedicine") ?: "Unknown"
            )
        }
    }
}