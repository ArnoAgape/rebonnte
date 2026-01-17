package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.firestore.Query

enum class MedicineSort(
    val firestoreField: MedicineOrderField,
    val firestoreDirection: Query.Direction
) {
    NAME_ASC(MedicineOrderField.NAME, Query.Direction.ASCENDING),
    NAME_DESC(MedicineOrderField.NAME, Query.Direction.DESCENDING),

    STOCK_ASC(MedicineOrderField.STOCK, Query.Direction.ASCENDING),
    STOCK_DESC(MedicineOrderField.STOCK, Query.Direction.DESCENDING),

    DATE_NEWEST(MedicineOrderField.CREATED_AT, Query.Direction.DESCENDING),
    DATE_OLDEST(MedicineOrderField.CREATED_AT, Query.Direction.ASCENDING)
}