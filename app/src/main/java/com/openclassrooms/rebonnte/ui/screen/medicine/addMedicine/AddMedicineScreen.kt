package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.common.FormEvent
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.fields.AisleDropdown
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.fields.DateTimeField
import com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine.fields.NumberOfMedicinesField
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: AddMedicineViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val resources = LocalResources.current
    val context = LocalContext.current
    val aisles = viewModel.aisles.collectAsState().value
    val selectedAisle = viewModel.selectedAisle.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                val result = snackbarHostState.showSnackbar(
                    message = resources.getString(event.message),
                    actionLabel = resources.getString(R.string.try_again),
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.addMedicine()
                }
            }

            is Event.ShowSuccessMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                onSaveClick()
            }

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_medicine)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->

        when (state.uiState) {
            is AddMedicineUiState.Idle, is AddMedicineUiState.Success -> {
                val aisleToDisplay =
                    if (state.uiState is AddMedicineUiState.Success) (state.uiState as AddMedicineUiState.Success).medicine
                    else state.medicine

                AddMedicineContent(
                    contentPadding = contentPadding,
                    dateTime = aisleToDisplay.dateTime,
                    onDateTimeChange = { viewModel.onAction(FormEvent.DateTimeChanged(it)) },
                    numberOfMedicines = aisleToDisplay.stock,
                    onNumberOfMedicinesChange = { newValue ->
                        viewModel.onAction(FormEvent.StockSet(newValue))
                    },
                    nameMedicine = aisleToDisplay.name,
                    onNameMedicineChanged = { viewModel.onAction(FormEvent.NameChanged(it)) },
                    aisles = aisles,
                    selectedAisle = selectedAisle,
                    onAisleSelected = { viewModel.onAction(FormEvent.AisleSelected(it)) },
                    onSaveClicked = { viewModel.addMedicine() },
                    isMedicineValid = state.isValid,
                    isLoading = false
                )
            }

            is AddMedicineUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.adding),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            is AddMedicineUiState.Error -> {
                val errorState = state.uiState as AddMedicineUiState.Error
                val message = when (errorState) {
                    is AddMedicineUiState.Error.NoAccount -> (state.uiState as AddMedicineUiState.Error.NoAccount).message
                    is AddMedicineUiState.Error.Generic -> (state.uiState as AddMedicineUiState.Error.Generic).message
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicineContent(
    contentPadding: PaddingValues = PaddingValues(),
    dateTime: Instant,
    onDateTimeChange: (Instant) -> Unit,
    numberOfMedicines: Int,
    onNumberOfMedicinesChange: (Int) -> Unit,
    nameMedicine: String,
    onNameMedicineChanged: (String) -> Unit,
    aisles: List<Aisle>,
    selectedAisle: Aisle?,
    onAisleSelected: (Aisle) -> Unit,
    onSaveClicked: () -> Unit,
    isMedicineValid: Boolean,
    isLoading: Boolean
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /** ---------- SCROLLABLE FORM CONTENT ---------- **/
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                /** ---------- DATE & TIME ---------- **/
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DateTimeField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dateTime,
                        onValueChange = onDateTimeChange,
                        label = stringResource(R.string.hint_datetime)
                    )
                }

                /** ---------- MEDICINE NAME ---------- **/
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = nameMedicine,
                    onValueChange = onNameMedicineChanged,
                    label = { Text(stringResource(id = R.string.hint_medicine_name)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )

                /** ---------- AISLE NAME ---------- **/
                AisleDropdown(
                    aisles = aisles,
                    selected = selectedAisle,
                    onSelect = onAisleSelected
                )

                /** ---------- NUMBER OF MEDICINES ---------- **/

                NumberOfMedicinesField(
                    numberOfMedicines = numberOfMedicines,
                    onNumberOfMedicinesChange = onNumberOfMedicinesChange
                )
            }

            /** ---------- SAVE BUTTON ---------- **/
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSaveClicked,
                enabled = isMedicineValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(id = R.string.action_add))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AddMedicineContentPreview() {
    val fakeAisles = listOf(
        Aisle(id = "1", name = "Paracetamol"),
        Aisle(id = "2", name = "Syrup"),
        Aisle(id = "3", name = "Cream")
    )
    RebonnteTheme {
        AddMedicineContent(
            dateTime = Instant.now(),
            onDateTimeChange = {},
            numberOfMedicines = 3,
            onNumberOfMedicinesChange = {},
            nameMedicine = "Doliprane",
            onNameMedicineChanged = {},
            aisles = fakeAisles,
            selectedAisle = fakeAisles.first(),
            onAisleSelected = {},
            onSaveClicked = {},
            isMedicineValid = true,
            isLoading = false
        )
    }
}