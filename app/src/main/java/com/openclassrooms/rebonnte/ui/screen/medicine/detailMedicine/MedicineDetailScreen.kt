package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.openclassrooms.rebonnte.ui.screen.addMedicine.fields.NumberOfMedicinesField
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.ui.utils.Format
import java.time.Instant

/**
 * Displays the detail view of a file.
 *
 * @param viewModel The ViewModel providing file data and state.
 * @param onBackClick Callback invoked when the back button is pressed.
 */
@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    viewModel: MedicineDetailViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(event.message),
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }
    }

    LaunchedEffect(state.medicineState) {
        (state.medicineState as? MedicineDetailUiState.Success)
            ?.medicine
            ?.let(viewModel::initStock)
    }

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
                            val medicine = (state.medicineState as MedicineDetailUiState.Success).medicine
                            Text(
                                text = medicine.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        else -> {}
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
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            state = refreshState,
            isRefreshing = state.medicineState is MedicineDetailUiState.Loading,
            onRefresh = { viewModel.refreshData() }
        ) {
            if (state.medicineState is MedicineDetailUiState.Success) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        DetailScreenContent(
                            medicine = (state.medicineState as MedicineDetailUiState.Success).medicine,
                            modifier = Modifier
                                .fillMaxWidth(),
                            numberOfMedicines = viewModel.stock.intValue,
                            onNumberOfMedicinesChange = viewModel::onStockChange,
                        )
                    }
                }
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

@Composable
fun DetailScreenContent(
    modifier: Modifier = Modifier,
    medicine: Medicine,
    numberOfMedicines: Int,
    onNumberOfMedicinesChange: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            /** ---------- NAME MEDICINE ---------- **/
            Text(
                text = stringResource(
                    R.string.medicine_name,
                    medicine.name
                )
            )

            Spacer(modifier.height(6.dp))

            /** ---------- NAME AISLE ---------- **/
            Text(
                text = stringResource(
                    R.string.aisle_name,
                    medicine.aisleName
                )
            )

            /** ---------- STOCK ---------- **/
            NumberOfMedicinesField(
                numberOfMedicines = numberOfMedicines,
                onNumberOfMedicinesChange = onNumberOfMedicinesChange
            )

            /** ---------- HISTORY ---------- **/
            Text(
                text = stringResource(R.string.history)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DetailScreenPreview() {
    RebonnteTheme {
        DetailScreenContent(
            medicine = Medicine(
                name = "Doliprane",
                stock = 7,
                aisleName = "Paracetamol"
            ),
            numberOfMedicines = 7,
            onNumberOfMedicinesChange = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HistoryDetailScreenPreview() {
    RebonnteTheme {
        DetailScreenContent(
            medicine = Medicine(
                name = "Doliprane",
                stock = 7,
                aisleName = "Paracetamol"
            ),
            numberOfMedicines = 7,
            onNumberOfMedicinesChange = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HistoryScreenPreview() {
    RebonnteTheme {
        DetailHistoryContent(
            history =
                History(
                    medicineName = "Doliprane",
                    author = User(
                        displayName = "John Doe"
                    ),
                    dateTime = Instant.now(),
                    details = "Details"
                )
        )
    }
}