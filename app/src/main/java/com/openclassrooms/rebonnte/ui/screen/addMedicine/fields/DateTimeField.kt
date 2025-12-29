package com.openclassrooms.rebonnte.ui.screen.addMedicine.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeField(
    modifier: Modifier = Modifier,
    value: Instant,
    onValueChange: (Instant) -> Unit,
    label: String
) {
    val zone = remember { ZoneId.systemDefault() }

    val formatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    val displayDate = remember(value) {
        value.atZone(zone).format(formatter)
    }

    var showDatePicker by remember { mutableStateOf(false) }

    PickerField(
        modifier = modifier,
        label = label,
        value = displayDate,
        icon = Icons.Default.DateRange,
        onClick = { showDatePicker = true }
    )

    val todayUtcMillis = remember {
        LocalDate.now(zone)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }

    val initialUtcMillis = remember(value) {
        value.atZone(zone)
            .toLocalDate()
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initialUtcMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >= todayUtcMillis
        }
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis
                        ?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                                .atZone(zone)
                                .toLocalDate()
                                .atStartOfDay(zone)
                                .toInstant()

                            onValueChange(instant)
                        }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}