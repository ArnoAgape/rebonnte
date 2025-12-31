package com.openclassrooms.rebonnte.ui.screen.aisle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun AisleScreen(
    viewModel: AisleViewModel,
    onAisleClick: (Aisle) -> Unit
) {
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(aisles) { aisle ->
            AisleContent(
                aisles = listOf(aisle),
                onAisleClick = onAisleClick
            )
        }
    }
}

@Composable
fun AisleContent(
    modifier: Modifier = Modifier,
    aisles: List<Aisle>,
    onAisleClick: (Aisle) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(aisles) { aisle ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAisleClick(aisle) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = aisle.name, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arrow"
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AisleContentPreview() {
    RebonnteTheme {
        AisleContent(
            aisles = listOf(
                Aisle(
                    name = "Paracetamol"
                ),
                Aisle(
                    name = "Antibiotic"
                ),
                Aisle(
                    name = "Antiseptic"
                )
            ),
            onAisleClick = {}
        )
    }
}