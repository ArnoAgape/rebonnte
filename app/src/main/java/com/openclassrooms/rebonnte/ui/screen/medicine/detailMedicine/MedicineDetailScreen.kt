package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.StockChangeType
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.components.ConfirmDeleteDialog
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.ui.utils.Format

/**
 * Displays the detail view of a file.
 *
 * @param viewModel The ViewModel providing file data and state.
 * @param onBackClick Callback invoked when the back button is pressed.
 */
@Composable
fun MedicineDetailScreen(
    viewModel: MedicineDetailViewModel,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit
) {

    val resources = LocalResources.current
    val context = LocalContext.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                snackbarHostState.showSnackbar(
                    message = resources.getString(event.message),
                    duration = SnackbarDuration.Short
                )
            }

            is Event.ShowSuccessMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                onBackClick()
            }

        }
    }

    val medicine = (state.medicineState as? MedicineDetailUiState.Success)?.medicine

    MedicineDetailContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onRefresh = { viewModel.refreshData() },
        onDeleteSelected = { viewModel.deleteMedicine() },
        onEditClick = {
            medicine?.let { onEditClick(it.id) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailContent(
    state: MedicineDetailScreenState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onRefresh: () -> Unit,
    onDeleteSelected: () -> Unit
) {

    val refreshState = rememberPullToRefreshState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    when (state.medicineState) {
                        is MedicineDetailUiState.Success -> {
                            Text(
                                text = state.medicineState.medicine.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        else -> Text(stringResource(R.string.medicine))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                },
                actions = {
                    if (state.medicineState is MedicineDetailUiState.Success) {
                        IconButton(
                            onClick = onEditClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_medicine)
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_medicine)
                            )
                        }
                    }
                }
            )
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
            when (val ui = state.medicineState) {

                is MedicineDetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // ----- MEDICINE DETAILS -----
                        MedicineHeaderContent(medicine = ui.medicine)

                        // ----- HISTORY -----
                        HistorySection(
                            historyState = state.historyState,
                            modifier = Modifier
                                .weight(1f, fill = false)
                        )
                    }
                }

                is MedicineDetailUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is MedicineDetailUiState.Error.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.error_medicine_not_found))
                    }
                }

                is MedicineDetailUiState.Error.Generic -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.error_loading))
                    }
                }

                is MedicineDetailUiState.Deleted -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.success_deleted_medicine))
                    }
                }
            }
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                show = true,
                onConfirm = {
                    showDeleteDialog = false
                    onDeleteSelected()
                },
                onDismiss = { showDeleteDialog = false },
                confirmButtonTitle = stringResource(R.string.confirm_delete_medicine),
                confirmButtonMessage = stringResource(R.string.confirm_delete_message_current_medicine)
            )
        }

    }
}

@Composable
fun MedicineHeaderContent(
    medicine: Medicine,
    modifier: Modifier = Modifier
) {

    val date = Format.getShortLocalizedDate(medicine.dateTime)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔹 MEDICINE NAME
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 🔹 AISLE + STOCK
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Aisle
                Text(
                    text = medicine.aisleName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Date
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Stock
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        text = stringResource(R.string.in_stock, medicine.stock),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HistorySection(
    historyState: HistoryDetailUiState,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {

        // ----- HEADER -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isExpanded)
                    Icons.Default.ExpandLess
                else
                    Icons.Default.ExpandMore,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ----- CONTENT -----
        AnimatedVisibility(visible = isExpanded) {
            when (historyState) {

                is HistoryDetailUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyState.history) { item ->
                            DetailHistoryContent(history = item)
                        }
                    }
                }

                is HistoryDetailUiState.Loading ->
                    CircularProgressIndicator()

                is HistoryDetailUiState.Error.Empty ->
                    Text(historyState.message)

                is HistoryDetailUiState.Error.Generic ->
                    Text(historyState.message)
            }
        }
    }
}

@Composable
fun DetailHistoryContent(
    history: History,
    modifier: Modifier = Modifier
) {
    val isAdded = history.changeType == StockChangeType.ADDED

    val actionColor = if (isAdded)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.error

    val actionText = when (history.changeType) {
        StockChangeType.ADDED ->
            stringResource(
                R.string.history_stock_added,
                history.quantity,
                history.medicineName
            )

        StockChangeType.REMOVED ->
            stringResource(
                R.string.history_stock_removed,
                history.quantity,
                history.medicineName
            )
    }

    val (date, time) = Format.getLocalizedDateParts(history.dateTime)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // ACTION
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyLarge,
                color = actionColor
            )

            // AUTHOR + DATE
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                history.author?.displayName?.let {
                    Text(
                        text = stringResource(R.string.by, it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = stringResource(R.string.updated_on, date, time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DetailScreenPreview() {
    RebonnteTheme {

        val fakeMedicine =
            Medicine(
                name = "CreamVe",
                aisleName = "Cream",
                stock = 10
            )

        val previewState = MedicineDetailScreenState(
            medicineState = MedicineDetailUiState.Success(fakeMedicine)
        )

        MedicineDetailContent(
            state = previewState,
            onBackClick = {},
            onEditClick = {},
            onRefresh = {},
            onDeleteSelected = {}
        )
    }
}