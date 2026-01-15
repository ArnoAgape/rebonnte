package com.openclassrooms.rebonnte.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.common.SelectionState
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun MedicineItem(
    modifier: Modifier = Modifier,
    medicines: List<Medicine>,
    onMedicineClick: (Medicine) -> Unit,
    selectionState: SelectionState,
    onToggleSelection: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(medicines) { medicine ->
            SelectItemRow(
                id = medicine.id,
                isSelectionMode = selectionState.isSelectionMode,
                isSelected = selectionState.selectedIds.contains(medicine.id),
                onSelectToggle = { onToggleSelection(medicine.id) },
                onClick = { onMedicineClick(medicine) }
            ) {
                Column {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = pluralStringResource(R.plurals.stock_quantity, medicine.stock, medicine.stock)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MedicineItemPreview() {
    RebonnteTheme {
        MedicineItem(
            medicines = listOf(
                Medicine(
                    name = "Painkiller",
                    stock = 10
                ),
                Medicine(
                    name = "CreamVe",
                    stock = 10
                ),
            ),
            onMedicineClick = {},
            selectionState = SelectionState(),
            onToggleSelection = {}
        )
    }
}