package com.openclassrooms.rebonnte.domain.model

enum class MedicineOrderField(val firestoreField: String) {
    NAME("name"),
    STOCK("stock"),
    CREATED_AT("createdAt")
}