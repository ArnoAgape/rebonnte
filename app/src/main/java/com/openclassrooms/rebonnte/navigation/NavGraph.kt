package com.openclassrooms.rebonnte.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.openclassrooms.rebonnte.ui.screen.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.screen.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.screen.detailAisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.screen.detailMedicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineViewModel

/**
 * Defines the navigation graph for the application.
 * Each destination binds its screen with the appropriate ViewModel.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val aisleViewModel: AisleViewModel = hiltViewModel()
    val medicineViewModel: MedicineViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AisleRoute
    ) {

        composable<AisleRoute> {
            AisleScreen(
                viewModel = aisleViewModel,
                onAisleClick = { aisle -> navController.navigate(DetailAisle(aisle.name))}
            )
        }

        composable<MedicineRoute> {
            MedicineScreen(
                viewModel = medicineViewModel,
                onMedicineClick = { medicine -> navController.navigate(DetailMedicine(medicine.name))}
            )
        }

        composable<DetailAisle> { backStackEntry ->
            val args = backStackEntry.toRoute<DetailAisle>()

            AisleDetailScreen(
                viewModel = hiltViewModel<MedicineViewModel>(),
                aisleName = args.aisleId,
                onMedicineClick = { medicine ->
                    navController.navigate(
                        DetailMedicine(medicine.name)
                    )
                }
            )
        }

        composable<DetailMedicine> {
            MedicineDetailScreen(
                viewModel = hiltViewModel<MedicineViewModel>(),
                name = it.arguments?.getString("nameMedicine") ?: "Unknown"
            )
        }
    }
}