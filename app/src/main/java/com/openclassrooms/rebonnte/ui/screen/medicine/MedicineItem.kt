package com.openclassrooms.rebonnte.ui.screen.medicine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.ui.screen.addMedicine.fields.NumberOfMedicinesField
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun MedicineItem(
    modifier: Modifier = Modifier,
    medicine: Medicine,
    numberOfMedicines: Int,
    onNumberOfMedicinesChange: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            /** ---------- NAME MEDICINE ---------- **/
            Text(
                text = stringResource(
                    R.string.medicine_name,
                    medicine.name
                )
            )

            Spacer(modifier.height(6.dp))

            /** ---------- NAME AISLE ---------- **/
            Text(
                text = stringResource(
                    R.string.aisle_name,
                    medicine.nameAisle
                )
            )

            /** ---------- STOCK ---------- **/
            NumberOfMedicinesField(
                numberOfMedicines = numberOfMedicines,
                onNumberOfMedicinesChange = onNumberOfMedicinesChange
            )

            /** ---------- HISTORY ---------- **/
            Text(
                text = stringResource(R.string.history)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DetailScreenPreview() {
    RebonnteTheme {
        MedicineItem(
            medicine = Medicine(
                name = "Doliprane",
                stock = 7,
                nameAisle = "Paracetamol"
            ),
            numberOfMedicines = 7,
            onNumberOfMedicinesChange = {},
        )
    }
}