package com.openclassrooms.rebonnte.ui.common.components

import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonTitle: String,
    confirmButtonMessage: String
) {
    if (show) {
        ConfirmDialog(
            title = confirmButtonTitle,
            message = confirmButtonMessage,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}