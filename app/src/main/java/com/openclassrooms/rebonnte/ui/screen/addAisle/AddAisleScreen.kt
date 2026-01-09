package com.openclassrooms.rebonnte.ui.screen.addAisle

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.common.Event
import com.openclassrooms.rebonnte.ui.common.EventsEffect
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAisleScreen(
    viewModel: AddAisleViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    EventsEffect(viewModel.eventsFlow) { event ->
        when (event) {
            is Event.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                onSaveClick()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_aisle)) },
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
            is AddAisleUiState.Idle, is AddAisleUiState.Success -> {
                val medicineToDisplay =
                    if (state.uiState is AddAisleUiState.Success) (state.uiState as AddAisleUiState.Success).aisle
                    else state.aisle

                AddAisleContent(
                    contentPadding = contentPadding,
                    aisle = medicineToDisplay.name,
                    onAisleNameChanged = { viewModel.onNameChanged(it) },
                    onSaveClicked = { viewModel.addAisle() },
                    isAisleValid = state.isValid,
                    isLoading = false
                )
            }

            is AddAisleUiState.Loading -> {
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
                            text = stringResource(R.string.sending),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            is AddAisleUiState.Error -> {
                val errorState = state.uiState as AddAisleUiState.Error
                val message = when (errorState) {
                    is AddAisleUiState.Error.NoAccount -> (state.uiState as AddAisleUiState.Error.NoAccount).message
                    is AddAisleUiState.Error.Generic -> (state.uiState as AddAisleUiState.Error.Generic).message
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
private fun AddAisleContent(
    contentPadding: PaddingValues = PaddingValues(),
    aisle: String,
    onAisleNameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    isAisleValid: Boolean,
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

                /** ---------- AISLE NAME ---------- **/
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = aisle,
                    onValueChange = onAisleNameChanged,
                    label = { Text(stringResource(id = R.string.hint_aisle_name)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )

                /** ---------- SAVE BUTTON ---------- **/
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveClicked,
                    enabled = isAisleValid && !isLoading
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
}

@PreviewLightDark
@Composable
private fun AddAisleContentPreview() {
    RebonnteTheme {
        AddAisleContent(
            aisle = "Aisle 1",
            onAisleNameChanged = {},
            onSaveClicked = {},
            isAisleValid = true,
            isLoading = false
        )
    }
}