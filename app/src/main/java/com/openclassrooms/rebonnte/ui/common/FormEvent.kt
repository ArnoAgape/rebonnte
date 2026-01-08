package com.openclassrooms.rebonnte.ui.common

import java.time.Instant

/**
 * A sealed class representing different events that can occur on a form.
 */
sealed class FormEvent {

    data class NameChanged(val name: String) : FormEvent()
    data class StockSet(val stock: Int) : FormEvent()
    data class DateTimeChanged(val dateTime: Instant) : FormEvent()
    data class NameAisleChanged(val nameAisle: String) : FormEvent()
    data class DisplayNameChanged(val displayName: String) : FormEvent()
    data class EmailChanged(val email: String) : FormEvent()
}