package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun MedicineDetailScreen(
    medicineName: String,
    viewModel: MedicineDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        when (uiState) {

            is MedicineDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MedicineDetailUiState.Success -> {
                val medicine =
                    (uiState as MedicineDetailUiState.Success)
                        .medicines
                        .firstOrNull { it.name == medicineName }

                if (medicine == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Médicament introuvable")
                    }
                } else {
                    MedicineDetailContent(
                        medicine = medicine,
                        onIncrement = { viewModel.incrementStock(medicine.name) },
                        onDecrement = { viewModel.decrementStock(medicine.name) },
                        paddingValues = paddingValues
                    )
                }
            }

            is MedicineDetailUiState.Error.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun médicament disponible")
                }
            }

            is MedicineDetailUiState.Error.Generic -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Erreur de chargement")
                }
            }

            else -> {}
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = history.medicineName,
                style = MaterialTheme.typography.titleSmall
            )
            Text("User: ${history.userId}")
            Text("Date: ${history.date}")
            Text("Details: ${history.details}")
        }
    }
}

@Composable
private fun MedicineDetailContent(
    medicine: Medicine,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {

        TextField(
            value = medicine.name,
            onValueChange = {},
            enabled = false,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDecrement) {
                Icon(Icons.Filled.KeyboardArrowDown, null)
            }

            Text(
                text = medicine.stock.toString(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(onClick = onIncrement) {
                Icon(Icons.Default.KeyboardArrowUp, null)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "History",
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn {
            items(medicine.histories) { history ->
                HistoryItem(history)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HistoryItemPreview() {
    RebonnteTheme {
        HistoryItem(
            history = History(
                medicineName = "Medicine 1",
                userId = "user123",
                date = "2023-07-01",
                details = "Updated medicine details"
            )
        )
    }
}