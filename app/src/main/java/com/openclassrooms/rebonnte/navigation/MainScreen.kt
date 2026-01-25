package com.openclassrooms.rebonnte.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel

/**
 * Root composable hosting the main navigation structure.
 *
 * Displays a bottom navigation bar on top-level destinations
 * and manages navigation state.
 */
@Composable
fun MainScreen() {

    // Navigation
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val loginViewModel: LoginViewModel = hiltViewModel()
    val isSignedIn by loginViewModel.isSignedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isSignedIn) {
        if (isSignedIn == false) {
            navController.navigate(Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // BottomBar screens
    val isBottomBarDestination =
        currentRoute == HomeAisle::class.qualifiedName ||
                currentRoute == HomeMedicine::class.qualifiedName ||
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
                        selected = currentRoute == destination.routeName,
                        onClick = {
                            if (currentRoute == destination.routeName) return@item

                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
    AISLE(Icons.Default.Home, R.string.aisle, HomeAisle),
    MEDICINE(Icons.AutoMirrored.Filled.List, R.string.medicine, HomeMedicine),
    PROFILE(Icons.Default.AccountBox, R.string.profile, Profile);

    @Composable
    fun label(): String = stringResource(id = labelRes)

    /** Class name used as navigation route. */
    val routeName: String get() = route::class.qualifiedName!!
}