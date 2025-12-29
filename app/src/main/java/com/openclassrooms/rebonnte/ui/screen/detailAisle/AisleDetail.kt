package com.openclassrooms.rebonnte.ui.screen.detailAisle

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineUiState
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    viewModel: MedicineViewModel,
    aisleName: String,
    onMedicineClick: (Medicine) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        when (uiState) {

            is MedicineUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MedicineUiState.Success -> {
                val medicines =
                    (uiState as MedicineUiState.Success)
                        .medicines
                        .filter { it.nameAisle == aisleName }

                LazyColumn(
                    contentPadding = paddingValues,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(medicines) { medicine ->
                        MedicineItem(
                            medicine = medicine,
                            onClick = onMedicineClick
                        )
                    }
                }
            }

            is MedicineUiState.Error.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun médicament dans cette allée")
                }
            }

            is MedicineUiState.Error.Generic -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Erreur de chargement")
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun MedicineItem(
    medicine: Medicine,
    onClick: (Medicine) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick(medicine) }
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
                contentDescription = null
            )
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