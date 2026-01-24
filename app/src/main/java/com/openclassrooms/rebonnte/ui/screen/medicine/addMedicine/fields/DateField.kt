package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.common.components.PickerField
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Clickable date field displaying a formatted date and
 * opening a date picker dialog on interaction.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    modifier: Modifier = Modifier,
    value: Instant,
    onValueChange: (Instant) -> Unit,
    label: String
) {

    val zone = ZoneId.systemDefault()

    // Convert Instant -> LocalDate
    val currentDate = value.atZone(zone).toLocalDate()

    var showDatePicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val display = value.atZone(zone).format(formatter)

    PickerField(
        modifier = modifier,
        label = label,
        value = display,
        icon = Icons.Default.DateRange,
        onClick = { showDatePicker = true }
    )

    // --- DatePicker ---
    if (showDatePicker) {

        val initialMillis = currentDate
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val utcMillis = dateState.selectedDateMillis
                        if (utcMillis != null) {

                            val pickedDate = Instant.ofEpochMilli(utcMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()

                            val resultInstant = pickedDate
                                .atStartOfDay(zone)
                                .toInstant()

                            onValueChange(resultInstant)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}