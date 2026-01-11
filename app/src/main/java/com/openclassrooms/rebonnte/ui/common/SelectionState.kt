package com.openclassrooms.rebonnte.ui.common

data class SelectionState(
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<String> = emptySet()
)