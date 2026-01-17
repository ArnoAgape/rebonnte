package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.navigation.EmbeddedSearchBar
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.components.ConfirmDeleteDialog
import com.openclassrooms.rebonnte.ui.common.components.MedicineItem
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun DetailAisleScreen(
    viewModel: DetailAisleViewModel,
    onMedicineClick: (Medicine) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
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

    AisleDetailContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onMedicineClick = onMedicineClick,
        onRefresh = { viewModel.refreshAisle() },
        onToggleSelection = { viewModel.toggleSelection(it) },
        onEnterSelectionMode = { viewModel.enterSelectionMode() },
        onExitSelectionMode = { viewModel.exitSelectionMode() },
        onDeleteSelected = { viewModel.deleteSelectedMedicines() },
        onSearchClick = { viewModel.activateSearch() },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onSearchClose = { viewModel.deactivateSearch() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailContent(
    state: AisleDetailScreenState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    onMedicineClick: (Medicine) -> Unit,
    onRefresh: () -> Unit = {},
    onToggleSelection: (String) -> Unit,
    onEnterSelectionMode: () -> Unit,
    onExitSelectionMode: () -> Unit,
    onDeleteSelected: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
) {

    val refreshState = rememberPullToRefreshState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.isSearchActive) {
        if (state.isSearchActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar =
            {
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
                            .focusRequester(focusRequester)
                    )
                } else {
                    TopAppBar(
                        title = {
                            when (state.uiState) {
                                is DetailAisleUiState.Success ->
                                    Text(
                                        text = state.uiState.aisle.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                else -> Text(stringResource(R.string.aisle))
                            }
                        },
                        actions = {
                            IconButton(onClick = onSearchClick) {
                                Icon(Icons.Rounded.Search, contentDescription = stringResource(R.string.contentDescription_button_search_medicines))
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
                                        contentDescription = stringResource(R.string.contentDescription_button_delete)
                                    )
                                }
                            } else if (state.uiState is DetailAisleUiState.Success) {
                                IconButton(onClick = onEnterSelectionMode) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.contentDescription_button_delete))
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.contentDescription_go_back)
                                )
                            }
                        }
                    )
                }
            }
    ) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = refreshState,
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {

            when (val ui = state.uiState) {

                is DetailAisleUiState.Success -> {
                    MedicineItem(
                        medicines = ui.medicines,
                        onMedicineClick = onMedicineClick,
                        selectionState = state.selection,
                        onToggleSelection = onToggleSelection
                    )
                }

                is DetailAisleUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DetailAisleUiState.Error.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_medicines_listed_in_aisle))
                    }
                }

                is DetailAisleUiState.Error.Generic -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.error_loading))
                    }
                }
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
fun AisleDetailScreenPreview() {
    RebonnteTheme {
        val fakeAisle = Aisle(name = "Painkillers")
        val fakeMedicines = listOf(
            Medicine(name = "CreamVe", stock = 10),
            Medicine(name = "Painkiller", stock = 5)
        )
        val previewState = AisleDetailScreenState(
            uiState = DetailAisleUiState.Success(fakeAisle, fakeMedicines)
        )

        AisleDetailContent(
            state = previewState,
            onBackClick = {},
            onMedicineClick = {},
            onSearchClick = {},
            onToggleSelection = {},
            onEnterSelectionMode = {},
            onExitSelectionMode = {},
            onDeleteSelected = {},
            onSearchQueryChange = {},
            onSearchClose = {}
        )
    }
}