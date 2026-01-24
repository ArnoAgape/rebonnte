package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle

/**
 * Dropdown component allowing the user to select an aisle.
 *
 * Displays the currently selected aisle and shows the list
 * of available aisles in an exposed dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDropdown(
    aisles: List<Aisle>,
    selected: Aisle?,
    onSelect: (Aisle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.aisle)) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            aisles.forEach { aisle ->
                DropdownMenuItem(
                    text = { Text(aisle.name) },
                    onClick = {
                        onSelect(aisle)
                        expanded = false
                    }
                )
            }
        }
    }
}