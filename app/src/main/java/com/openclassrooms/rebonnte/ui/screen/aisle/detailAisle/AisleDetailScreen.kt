package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineItem
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@SuppressLint("LocalContextGetResourceValueCall")
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

            else -> {}
        }
    }

    AisleDetailContent(
        state = state,
        onBackClick = onBackClick,
        onMedicineClick = onMedicineClick,
        onRefresh = { viewModel.refreshAisle() },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailContent(
    state: AisleDetailUiState,
    onBackClick: () -> Unit,
    onMedicineClick: (Medicine) -> Unit,
    onRefresh: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    when (state) {
                        is AisleDetailUiState.Success ->
                            Text(
                                text = state.aisle.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                        else -> Text("Aisle")
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
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = refreshState,
            isRefreshing = state is AisleDetailUiState.Loading,
            onRefresh = onRefresh
        ) {

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
                    MedicineItem(
                        medicines = state.medicines,
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
                        Text(stringResource(R.string.no_medicines_listed_in_aisle))
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
            }
        }
    }
}

@PreviewLightDark
@Composable
fun AisleDetailScreenPreview() {
    RebonnteTheme {
        val fakeAisle = Aisle(name = "Painkillers")
        val fakeMedicines = listOf(
            Medicine(name = "Doliprane", stock = 10),
            Medicine(name = "Ibuprofène", stock = 5)
        )

        AisleDetailContent(
            state = AisleDetailUiState.Success(
                aisle = fakeAisle,
                medicines = fakeMedicines
            ),
            onBackClick = {},
            onMedicineClick = {}
        )
    }
}