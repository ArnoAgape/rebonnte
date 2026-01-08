package com.openclassrooms.rebonnte.ui.screen.addMedicine

import java.time.Instant

/**
 * A sealed class representing different events that can occur on a form.
 */
sealed class AddMedicineFormEvent {

    data class NameChanged(val name: String) : AddMedicineFormEvent()
    data class StockSet(val stock: Int) : AddMedicineFormEvent()
    data class DateTimeChanged(val dateTime: Instant) : AddMedicineFormEvent()
    data class NameAisleChanged(val nameAisle: String) : AddMedicineFormEvent()
}