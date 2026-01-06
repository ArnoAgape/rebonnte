package com.openclassrooms.rebonnte.ui.screen.medicine.detailMedicine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import java.time.Instant

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = history.medicineName,
                style = MaterialTheme.typography.titleLarge
            )
            Text("User: ${history.author}")
            Text("Date: ${history.dateTime}")
            Text("Details: ${history.details}")
        }
    }
}

@PreviewLightDark
@Composable
private fun HistoryItemPreview() {
    RebonnteTheme {
        HistoryItem(
            history = History(
                medicineName = "Medicine 1",
                author = null,
                dateTime = Instant.now(),
                details = "Updated medicine details"
            )
        )
    }
}