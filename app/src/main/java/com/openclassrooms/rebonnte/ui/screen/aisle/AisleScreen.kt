package com.openclassrooms.rebonnte.ui.screen.aisle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(aisles) { aisle ->
            AisleItem(
                aisle = aisle,
                onClick = onAisleClick
            )
        }
    }
}

@Composable
fun AisleItem(
    aisle: Aisle,
    onClick: (Aisle) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(aisle) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = aisle.name, style = MaterialTheme.typography.bodyMedium)
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Arrow")
        }
    }
}

@PreviewLightDark
@Composable
private fun AisleItemPreview() {
    RebonnteTheme {
        AisleItem(
            aisle = Aisle(
                name = "Aisle 1"
            ),
            onClick = {}
        )
    }
}