package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.components.ConfirmDeleteDialog
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.ui.utils.Format
import java.time.Instant

/**
 * Displays the detail view of a file.
 *
 * @param viewModel The ViewModel providing file data and state.
 * @param onBackClick Callback invoked when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(25.dp)
                    ) {

                        // ----- MEDICINE DETAILS -----
                        item {
                            MedicineHeaderContent(
                                medicine = ui.medicine
                            )
                        }

                        // ----- HISTORY -----
                        item {
                            HistorySection(
                                historyState = state.historyState
                            )
                        }
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
fun MedicineHeaderContent(medicine: Medicine) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = medicine.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(stringResource(R.string.aisle_name, medicine.aisleName))
        Text(stringResource(R.string.in_stock, medicine.stock))
    }
}

@Composable
fun HistorySection(
    historyState: HistoryDetailUiState
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column {

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

        // ----- CONTENT -----
        AnimatedVisibility(visible = isExpanded) {
            when (historyState) {

                is HistoryDetailUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
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
    modifier: Modifier = Modifier,
    history: History
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            /** ---------- USER ---------- **/
            history.author?.displayName?.let {
                Text(
                    text = stringResource(
                        R.string.by,
                        history.author.displayName
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            /** ---------- NAME MEDICINE ---------- **/
            Text(
                text = stringResource(
                    R.string.medicine_name,
                    history.medicineName
                )
            )

            /** ---------- DATE ---------- **/
            val (date, time) = Format.getLocalizedDateParts(history.dateTime)
            Text(
                text = stringResource(
                    R.string.updated_on,
                    date, time
                )
            )

            /** ---------- DETAILS ---------- **/
            Text(
                text = stringResource(
                    R.string.medicine_details,
                    history.details
                )
            )
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

        val fakeHistory = listOf(
            History(
                author =
                    User(
                        displayName = "John Doe"
                    ),
                medicineName = "CreamVe",
                dateTime = Instant.now(),
                details = "New model of the cream"

            ),
            History(
                author =
                    User(
                        displayName = "Donald Duck"
                    ),
                medicineName = "CreamVe",
                dateTime = Instant.now(),
                details = "10 creams CreamVe sold"

            )
        )

        val previewState = MedicineDetailScreenState(
            medicineState = MedicineDetailUiState.Success(fakeMedicine),
            historyState = HistoryDetailUiState.Success(fakeHistory)
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