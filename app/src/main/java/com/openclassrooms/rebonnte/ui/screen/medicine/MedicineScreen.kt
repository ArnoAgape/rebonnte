package com.openclassrooms.rebonnte.ui.screen.medicine

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel,
    onMedicineClick: (Medicine) -> Unit
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())

    LazyColumn(
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

@Composable
fun MedicineItem(
    medicine: Medicine,
    onClick: (Medicine) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(medicine) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = medicine.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Stock: ${medicine.stock}", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Arrow")
        }
    }
}

@PreviewLightDark
@Composable
private fun MedicineItemPreview() {
    RebonnteTheme {
        MedicineItem(
            medicine = Medicine(
                "Medicine 1",
                10,
                "Aisle 1",
                emptyList()
            ),
            onClick = {}
        )
    }
}
