package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.navigation.EmbeddedSearchBar
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.components.ConfirmDeleteDialog
import com.openclassrooms.rebonnte.ui.common.components.MedicineItem
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun MedicineHomeScreen(
    viewModel: MedicineHomeViewModel,
    onMedicineClick: (Medicine) -> Unit,
    onFABClick: () -> Unit
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                snackbarHostState.showSnackbar(
                    message = resources.getString(event.message),
                    duration = SnackbarDuration.Short
                )
            }

            else -> {}
        }
    }

    MedicineHomeContent(
        state = state,
        onMedicineClick = onMedicineClick,
        onFABClick = onFABClick,
        onSearchClick = { viewModel.activateSearch() },
        onRefresh = { viewModel.refreshMedicines() },
        isSignedIn = isSignedIn == true,
        onToggleSelection = { viewModel.toggleSelection(it) },
        onEnterSelectionMode = { viewModel.enterSelectionMode() },
        onExitSelectionMode = { viewModel.exitSelectionMode() },
        onDeleteSelected = { viewModel.deleteSelectedMedicines() },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onSearchClose = { viewModel.deactivateSearch() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineHomeContent(
    state: MedicineHomeScreenState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onMedicineClick: (Medicine) -> Unit,
    onFABClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onRefresh: () -> Unit,
    isSignedIn: Boolean,
    onToggleSelection: (String) -> Unit,
    onEnterSelectionMode: () -> Unit,
    onExitSelectionMode: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val refreshState = rememberPullToRefreshState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            if (state.isSearchActive) {
                EmbeddedSearchBar(
                    query = state.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClose = onSearchClose,
                    modifier = Modifier
                        .padding(
                            top = 42.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.medicines)) },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        }
                        if (state.selection.isSelectionMode) {
                            IconButton(
                                onClick = {
                                    if (state.selection.selectedIds.isEmpty()) {
                                        onExitSelectionMode()
                                    } else {
                                        showDeleteDialog = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (state.selection.selectedIds.isEmpty())
                                        Icons.Default.Close
                                    else
                                        Icons.Default.DeleteForever,
                                    contentDescription = null
                                )
                            }
                        } else if (state.uiState is MedicineHomeUiState.Success) {
                            IconButton(onClick = onEnterSelectionMode) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isSignedIn) onFABClick()
                    else Toast.makeText(
                        context,
                        resources.getString(R.string.error_no_account_add_medicine),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.contentDescription_button_add)
                )
            }
        }
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            state = refreshState,
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {
            when (val ui = state.uiState) {

                is MedicineHomeUiState.Success -> {
                    MedicineItem(
                        medicines = ui.medicines,
                        onMedicineClick = onMedicineClick,
                        selectionState = state.selection,
                        onToggleSelection = onToggleSelection
                    )
                }

                is MedicineHomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MedicineHomeUiState.Error.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_medicines),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> Unit
            }
        }

        ConfirmDeleteDialog(
            show = showDeleteDialog,
            onConfirm = {
                showDeleteDialog = false
                onDeleteSelected()
            },
            onDismiss = { showDeleteDialog = false },
            confirmButtonTitle = stringResource(R.string.confirm_delete_medicine),
            confirmButtonMessage = stringResource(R.string.confirm_delete_message_medicines)
        )
    }
}

@PreviewLightDark
@Composable
fun MedicineHomeScreenPreview() {
    RebonnteTheme {
        // Fake medicines
        val sampleMedicines = listOf(
            Medicine(name = "Painkiller 1000 mg", stock = 12),
            Medicine(name = "Painkiller 400 mg", stock = 5),
            Medicine(name = "Vitamin C", stock = 20)
        )

        val previewState = MedicineHomeScreenState(
            uiState = MedicineHomeUiState.Success(sampleMedicines),
            isRefreshing = false
        )

        MedicineHomeContent(
            state = previewState,
            onMedicineClick = {},
            onFABClick = {},
            onSearchClick = {},
            onRefresh = {},
            isSignedIn = true,
            onToggleSelection = {},
            onEnterSelectionMode = {},
            onExitSelectionMode = {},
            onDeleteSelected = {},
            onSearchQueryChange = {},
            onSearchClose = {}
        )
    }
}