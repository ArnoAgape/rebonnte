package com.openclassrooms.rebonnte.ui.common.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.MedicineSort

/**
 * Dropdown menu allowing users to select a medicine sorting option.
 */
@Composable
fun MedicineSortMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSortSelected: (MedicineSort) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_name_asc)) },
            onClick = {
                onSortSelected(MedicineSort.NAME_ASC)
                onDismiss()
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_name_desc)) },
            onClick = {
                onSortSelected(MedicineSort.NAME_DESC)
                onDismiss()
            }
        )

        HorizontalDivider()

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_stock_asc)) },
            onClick = {
                onSortSelected(MedicineSort.STOCK_ASC)
                onDismiss()
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_stock_desc)) },
            onClick = {
                onSortSelected(MedicineSort.STOCK_DESC)
                onDismiss()
            }
        )

        HorizontalDivider()

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_date_newest)) },
            onClick = {
                onSortSelected(MedicineSort.DATE_NEWEST)
                onDismiss()
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_date_oldest)) },
            onClick = {
                onSortSelected(MedicineSort.DATE_OLDEST)
                onDismiss()
            }
        )
    }
}