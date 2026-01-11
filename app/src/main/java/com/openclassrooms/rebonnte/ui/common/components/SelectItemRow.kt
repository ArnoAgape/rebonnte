package com.openclassrooms.rebonnte.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SelectItemRow(
    id: String,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelectToggle: () -> Unit,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                if (isSelectionMode) onSelectToggle()
                else onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isSelectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectToggle() }
            )
            Spacer(Modifier.width(8.dp))
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}