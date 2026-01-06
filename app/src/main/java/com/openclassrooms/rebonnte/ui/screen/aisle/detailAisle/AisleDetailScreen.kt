package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    viewModel: AisleDetailViewModel,
    onMedicineClick: (Medicine) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
                    when (state) {
                        is AisleDetailUiState.Success -> {
                            val aisle = (state as AisleDetailUiState.Success).aisle
                            Text(
                                text = aisle.name,
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
    ) { paddingValues ->
        when (state) {

            is AisleDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AisleDetailUiState.Success -> {
                AisleContent(
                    medicines = ((state as AisleDetailUiState.Success).medicines),
                    onMedicineClick = onMedicineClick
                )
            }

            is AisleDetailUiState.Error.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No medicine in this aisle")
                }
            }

            is AisleDetailUiState.Error.Generic -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error while loading")
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun AisleContent(
    modifier: Modifier = Modifier,
    medicines: List<Medicine>,
    onMedicineClick: (Medicine) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(medicines) { medicine ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onMedicineClick(medicine) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Stock: ${medicine.stock}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arrow"
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MedicineItemPreview() {
    RebonnteTheme {
        AisleContent(
            medicines = listOf(
                Medicine(
                    name = "Doliprane",
                    stock = 7,
                    nameAisle = "Paracetamol",
                    histories = emptyList()
                ),
                Medicine(
                    name = "Clamoxyl",
                    stock = 10,
                    nameAisle = "Antibiotic",
                    histories = emptyList()
                ),
                Medicine(
                    name = "Biafine",
                    stock = 26,
                    nameAisle = "Antiseptic",
                    histories = emptyList()
                )
            ),
            onMedicineClick = {}
        )
    }
}