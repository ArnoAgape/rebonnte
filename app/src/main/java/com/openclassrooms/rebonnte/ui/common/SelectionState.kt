package com.openclassrooms.rebonnte.ui.common

/**
 * Represents the current selection state of a list.
 *
 * Used to track whether selection mode is enabled
 * and which items are currently selected.
 */
data class SelectionState(
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<String> = emptySet()
)