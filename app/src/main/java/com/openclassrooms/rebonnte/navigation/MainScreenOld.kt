package com.openclassrooms.rebonnte.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.screen.addMedicine.AddMedicineViewModel
import com.openclassrooms.rebonnte.ui.screen.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import androidx.compose.ui.res.stringResource

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenOld(
    onFABMedicineClick: () -> Unit,
    onFABAisleClick: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = hiltViewModel()
    val addMedicineViewModel: AddMedicineViewModel = hiltViewModel()
    val aisleViewModel: AisleViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isSignedIn by loginViewModel.isSignedIn.collectAsStateWithLifecycle()
    val route = navBackStackEntry?.destination?.route

    RebonnteTheme {
        Scaffold(
            topBar = {
                var isSearchActive by rememberSaveable { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                    TopAppBar(
                        title = { if (route == "aisle") Text(text = "Aisle") else Text(text = "Medicines") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        actions = {
                            var expanded by remember { mutableStateOf(false) }
                            if (currentRoute(navController) == "medicine") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = null)
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            offset = DpOffset(x = 0.dp, y = 0.dp)
                                        ) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    medicineViewModel.sortByNone()
                                                    expanded = false
                                                },
                                                text = { Text("Sort by None") }
                                            )
                                            DropdownMenuItem(
                                                onClick = {
                                                    medicineViewModel.sortByName()
                                                    expanded = false
                                                },
                                                text = { Text("Sort by Name") }
                                            )
                                            DropdownMenuItem(
                                                onClick = {
                                                    medicineViewModel.sortByStock()
                                                    expanded = false
                                                },
                                                text = { Text("Sort by Stock") }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                    if (currentRoute(navController) == "medicine") {
                        EmbeddedSearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                medicineViewModel.filterByName(it)
                                searchQuery = it
                            },
                            isSearchActive = isSearchActive,
                            onActiveChanged = { isSearchActive = it }
                        )
                    }
                }

            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Aisle") },
                        selected = currentRoute(navController) == "aisle",
                        onClick = { navController.navigate(AisleRoute) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Medicine") },
                        selected = currentRoute(navController) == "medicine",
                        onClick = { navController.navigate(MedicineRoute) }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (isSignedIn == true) {
                            if (route == "medicine") {
                                onFABMedicineClick()
                            } else if (route == "aisle") {
                                onFABAisleClick()
                            }
                        } else {
                            Toast.makeText(
                                context, stringResource(R.string.error_no_account_file),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AppNavGraph(navController = navController)
            }
        }
    }
}*/