package com.openclassrooms.rebonnte.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {

    // Navigation
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val loginViewModel: LoginViewModel = hiltViewModel()

    // BottomBar screens
    val isBottomBarDestination =
        currentRoute == AisleRoute::class.qualifiedName ||
                currentRoute == MedicineRoute::class.qualifiedName ||
                currentRoute == Profile::class.qualifiedName

    if (isBottomBarDestination) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = {
                            Icon(destination.icon, contentDescription = destination.label())
                        },
                        label = { Text(destination.label()) },
                        selected = currentRoute == destination.route,
                        onClick = {
                            if (destination == AppDestinations.PROFILE) {
                                if (loginViewModel.isSignedIn.value == true) {
                                    navController.navigate(Profile)
                                } else {
                                    navController.navigate(LoginRoute)
                                }
                            } else {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        )
        {
            AppNavGraph(navController = navController)
        }
    } else {
        AppNavGraph(navController = navController)
    }
}

enum class AppDestinations(
    val icon: ImageVector,
    private val labelRes: Int,
    val route: Any
) {
    AISLE(Icons.Default.Home, R.string.aisle, AisleRoute),
    MEDICINE(Icons.AutoMirrored.Filled.List, R.string.medicine, MedicineRoute),
    PROFILE(Icons.Default.AccountBox, R.string.profile, Profile);

    @Composable
    fun label(): String = stringResource(id = labelRes)
}