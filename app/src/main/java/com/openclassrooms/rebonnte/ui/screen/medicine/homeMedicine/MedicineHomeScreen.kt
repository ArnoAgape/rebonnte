package com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineItem
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineHomeScreen(
    viewModel: MedicineHomeViewModel,
    onMedicineClick: (Medicine) -> Unit,
    onFABClick: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val refreshState = rememberPullToRefreshState()
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    val noAccountMessage = stringResource(R.string.error_no_account_add_medicine)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.medicines))
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
            onRefresh = { viewModel.refreshMedicines() }
        ) {
            when (val ui = state.uiState) {
                is MedicineHomeUiState.Success ->
                    MedicineItem(
                        medicines = ui.medicines,
                        onMedicineClick = onMedicineClick
                    )

                is MedicineHomeUiState.Loading -> {
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

                is MedicineHomeUiState.Error.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_medicines),
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineHomeContent(
    state: MedicineHomeScreenState,
    onRefresh: () -> Unit,
    onMedicineClick: (Medicine) -> Unit,
    onFABClick: () -> Unit,
    isSignedIn: Boolean
) {
    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.medicines)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isSignedIn) onFABClick()
                    else Toast.makeText(
                        context,
                        context.getString(R.string.error_no_account_add_medicine),
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
                        onMedicineClick = onMedicineClick
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
    }
}

@PreviewLightDark
@Composable
fun MedicineHomeScreenPreview() {
    RebonnteTheme {
        // Fake medicines
        val sampleMedicines = listOf(
            Medicine(name = "Doliprane 1000mg", stock = 12),
            Medicine(name = "Ibuprofène 400mg", stock = 5),
            Medicine(name = "Efferalgan Vitamine C", stock = 20)
        )

        val previewState = MedicineHomeScreenState(
            uiState = MedicineHomeUiState.Success(sampleMedicines),
            isRefreshing = false
        )

        MedicineHomeContent(
            state = previewState,
            onRefresh = {},
            onMedicineClick = {},
            onFABClick = {},
            isSignedIn = true
        )
    }
}