package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.navigation.EmbeddedSearchBar
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.common.components.ConfirmDeleteDialog
import com.openclassrooms.rebonnte.ui.common.components.SelectItemRow
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AisleHomeScreen(
    viewModel: AisleHomeViewModel,
    onAisleClick: (Aisle) -> Unit,
    onFABClick: () -> Unit
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(event.message),
                    duration = SnackbarDuration.Short
                )
            }

            else -> {}
        }
    }

    AisleHomeContent(
        state = state,
        onAisleClick = onAisleClick,
        onFABClick = onFABClick,
        onSearchClick = { viewModel.activateSearch() },
        onRefresh = { viewModel.refreshAisles() },
        onToggleSelection = { viewModel.toggleSelection(it) },
        onEnterSelectionMode = { viewModel.enterSelectionMode() },
        onExitSelectionMode = { viewModel.exitSelectionMode() },
        onDeleteSelected = { viewModel.deleteSelectedAisles() },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onSearchClose = { viewModel.deactivateSearch() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleHomeContent(
    state: AisleHomeScreenState,
    onAisleClick: (Aisle) -> Unit,
    onFABClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onRefresh: () -> Unit,
    onToggleSelection: (String) -> Unit,
    onEnterSelectionMode: () -> Unit,
    onExitSelectionMode: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val noAccountMessage = stringResource(R.string.error_no_account_add_aisle)

    Scaffold(
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
                            end = 16.dp)
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.aisles)) },
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
                        } else if (state.uiState is AisleHomeUiState.Success) {
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
                    if (state.isSignedIn == true) onFABClick()
                    else Toast.makeText(context, noAccountMessage, Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
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

                is AisleHomeUiState.Success ->
                    AisleListContent(
                        aisles = ui.aisles,
                        onAisleClick = onAisleClick,
                        selectionState = state.selection,
                        onToggleSelection = onToggleSelection
                    )

                is AisleHomeUiState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                is AisleHomeUiState.Error.Empty ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_aisles))
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
            confirmButtonTitle = stringResource(R.string.confirm_delete_aisle),
            confirmButtonMessage = stringResource(R.string.confirm_delete_message_aisle)
        )
    }
}

@Composable
fun AisleListContent(
    aisles: List<Aisle>,
    onAisleClick: (Aisle) -> Unit,
    selectionState: SelectionState,
    onToggleSelection: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(aisles) { aisle ->
            SelectItemRow(
                id = aisle.id,
                isSelectionMode = selectionState.isSelectionMode,
                isSelected = selectionState.selectedIds.contains(aisle.id),
                onSelectToggle = { onToggleSelection(aisle.id) },
                onClick = { onAisleClick(aisle) }
            ) {
                Column {
                    Text(
                        text = aisle.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun AisleHomeScreenPreview() {
    RebonnteTheme {
        val aisles = listOf(
            Aisle(id = "1", name = "Painkillers"),
            Aisle(id = "2", name = "Antibiotics"),
            Aisle(id = "3", name = "Antiseptics")
        )

        AisleHomeContent(
            state = AisleHomeScreenState(
                uiState = AisleHomeUiState.Success(aisles),
                isRefreshing = false,
                selection = SelectionState(
                    isSelectionMode = true,
                    selectedIds = setOf("1", "3")
                ),
                isSignedIn = true
            ),
            onAisleClick = {},
            onFABClick = {},
            onSearchClick = {},
            onRefresh = {},
            onToggleSelection = {},
            onEnterSelectionMode = {},
            onExitSelectionMode = {},
            onDeleteSelected = {},
            onSearchQueryChange = {},
            onSearchClose = {}
        )
    }
}