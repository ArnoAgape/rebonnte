package com.openclassrooms.rebonnte.ui.screen.detailMedicine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.ui.screen.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun MedicineDetailScreen(
    name: String,
    viewModel: MedicineViewModel
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val medicine = medicines.find { it.name == name }

    if (medicine == null) {
        Text("Medicine not found")
        return
    }

    Scaffold { paddingValues ->
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

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(
                    onClick = { viewModel.decrementStock(medicine.name) }
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, null)
                }

                Text(
                    text = medicine.stock.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(
                    onClick = { viewModel.incrementStock(medicine.name) }
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, null)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("History", style = MaterialTheme.typography.titleLarge)

            LazyColumn {
                items(medicine.histories) { history ->
                    HistoryItem(history)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "User: ${history.userId}")
            Text(text = "Date: ${history.date}")
            Text(text = "Details: ${history.details}")
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