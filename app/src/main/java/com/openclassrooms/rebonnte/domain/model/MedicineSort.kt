package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.firestore.Query

enum class MedicineSort {
    NAME_ASC,
    NAME_DESC,
    STOCK_ASC,
    STOCK_DESC,
    DATE_NEWEST,
    DATE_OLDEST;

    fun sort(medicines: List<Medicine>): List<Medicine> =
        when (this) {

            NAME_ASC -> medicines.sortedBy { it.nameLowercase.ifBlank { it.name.lowercase() } }
            NAME_DESC -> medicines.sortedByDescending { it.nameLowercase.ifBlank { it.name.lowercase() } }

            STOCK_ASC -> medicines.sortedBy { it.stock }
            STOCK_DESC -> medicines.sortedByDescending { it.stock }

            DATE_NEWEST -> medicines.sortedByDescending { it.createdAt }
            DATE_OLDEST -> medicines.sortedBy { it.createdAt }

        }
}