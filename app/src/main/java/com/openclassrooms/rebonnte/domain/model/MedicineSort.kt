package com.openclassrooms.rebonnte.domain.model

/**
 * Defines available sorting strategies for medicines.
 *
 * Each value provides a sorting behavior based on
 * name, stock quantity, or creation date.
 */
enum class MedicineSort {
    NAME_ASC,
    NAME_DESC,
    STOCK_ASC,
    STOCK_DESC,
    DATE_NEWEST,
    DATE_OLDEST;

    /**
     * Sorts a list of medicines according to the selected strategy.
     */
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