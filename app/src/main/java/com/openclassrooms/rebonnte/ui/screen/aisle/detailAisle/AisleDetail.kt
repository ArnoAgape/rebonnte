package com.openclassrooms.rebonnte.ui.screen.aisle.detailAisle

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineHomeUiState
import com.openclassrooms.rebonnte.ui.screen.medicine.homeMedicine.MedicineHomeViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    viewModel: MedicineHomeViewModel,
    onMedicineClick: (Medicine) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        when (uiState) {

            is MedicineHomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MedicineHomeUiState.Success -> {
                MedicineContent(
                    medicines = (uiState as MedicineHomeUiState.Success).medicines,
                    onMedicineClick = onMedicineClick
                )
            }

            is MedicineHomeUiState.Error.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No medicine in this aisle")
                }
            }

            is MedicineHomeUiState.Error.Generic -> {
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
fun MedicineContent(
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
        MedicineContent(
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