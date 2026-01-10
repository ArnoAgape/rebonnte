package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.components.ConfirmDeleteDialog
import com.openclassrooms.rebonnte.ui.common.components.SelectItemRow
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AisleHomeScreen(
    viewModel: AisleHomeViewModel,
    onAisleClick: (Aisle) -> Unit,
    onFABClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectionState by viewModel.selection.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    val noAccountMessage = stringResource(R.string.error_no_account_add_aisle)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.aisles))
                },
                actions = {
                    if (selectionState.isSelectionMode) {
                        IconButton(
                            onClick = {
                                if (selectionState.selectedIds.isEmpty()) {
                                    viewModel.exitSelectionMode()
                                } else {
                                    showDeleteDialog = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectionState.selectedIds.isEmpty())
                                    Icons.Default.Close
                                else
                                    Icons.Default.DeleteForever,
                                contentDescription = stringResource(R.string.delete_aisle)
                            )
                        }
                    } else if (state.uiState is AisleHomeUiState.Success) {
                        // selection mode
                        IconButton(onClick = { viewModel.enterSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_aisle)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isSignedIn == true) {
                        onFABClick()
                    } else {
                        Toast.makeText(
                            context, noAccountMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
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
            onRefresh = { viewModel.refreshAisles() }
        ) {
            when (val ui = state.uiState) {
                is AisleHomeUiState.Success ->
                    AisleContent(
                        aisles = ui.aisles,
                        onAisleClick = onAisleClick,
                        selectionState = selectionState,
                        onToggleSelection = { viewModel.toggleSelection(it) }
                    )

                is AisleHomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is AisleHomeUiState.Error.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_aisles),
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                else -> {}
            }
        }

        ConfirmDeleteDialog(
            show = showDeleteDialog,
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteSelectedAisles()
            },
            onDismiss = { showDeleteDialog = false },
            confirmButtonTitle = stringResource(R.string.confirm_delete_aisle),
            confirmButtonMessage = stringResource(R.string.confirm_delete_message_aisle)
        )
    }
}

@Composable
fun AisleContent(
    modifier: Modifier = Modifier,
    aisles: List<Aisle>,
    onAisleClick: (Aisle) -> Unit,
    selectionState: AisleSelectionState,
    onToggleSelection: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(aisles) { aisle ->

            SelectItemRow(
                id = aisle.id,
                label = aisle.name,
                isSelectionMode = selectionState.isSelectionMode,
                isSelected = selectionState.selectedIds.contains(aisle.id),
                onSelectToggle = { onToggleSelection(aisle.id) },
                onClick = { onAisleClick(aisle) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AisleContentPreview() {
    RebonnteTheme {
        AisleContent(
            aisles = listOf(
                Aisle(
                    name = "Paracetamol"
                ),
                Aisle(
                    name = "Antibiotic"
                ),
                Aisle(
                    name = "Antiseptic"
                )
            ),
            onAisleClick = {},
            selectionState = AisleSelectionState(),
            onToggleSelection = { }
        )
    }
}