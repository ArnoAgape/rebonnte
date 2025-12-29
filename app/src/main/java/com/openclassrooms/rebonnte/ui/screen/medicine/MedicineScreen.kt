package com.openclassrooms.rebonnte.ui.screen.medicine

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.screen.detailAisle.MedicineItem
import com.openclassrooms.rebonnte.ui.screen.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.utils.Format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel,
    loginViewModel : LoginViewModel,
    onMedicineClick: (Medicine) -> Unit,
    onFABClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refreshState = rememberPullToRefreshState()
    val isSignedIn by loginViewModel.isSignedIn.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    val noAccountMessage = stringResource(R.string.error_no_account_medicine)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.home))
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
            isRefreshing = uiState is MedicineUiState.Loading,
            onRefresh = { viewModel.refreshMedicines() }
        ) {
            when (uiState) {
                is MedicineUiState.Idle, is MedicineUiState.Success ->
                    MedicineContent(
                        medicines = (uiState as MedicineUiState.Success).medicines,
                        onMedicineClick = onMedicineClick
                    )

                is MedicineUiState.Loading -> {
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

                is MedicineUiState.Error.Empty -> {
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

@Composable
private fun MedicineContent(
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
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {

                    // ---- DATE/TIME ----
                    val (date, time) = Format.getLocalizedDateParts(medicine.dateTime)

                    Text(stringResource(R.string.added_at, date, time))

                    Spacer(Modifier.height(8.dp))

                    // ---- NAME ----
                    Text(
                        text = stringResource(
                            R.string.by,
                            medicine.author?.displayName.toString()
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
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
        MedicineItem(
            medicine = Medicine(
                name = "Medicine 1",
                stock = 10,
                nameAisle = "Aisle 1",
                histories = emptyList()
            ),
            onClick = {}
        )
    }
}
