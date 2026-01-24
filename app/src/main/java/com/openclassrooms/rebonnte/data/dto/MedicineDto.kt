package com.openclassrooms.rebonnte.data.dto

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.User
import java.io.Serializable

/**
 * Data Transfer Object representing an aisle stored in Firestore.
 *
 * Used for persistence and network transport.
 */
data class MedicineDto(
    val id : String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val stock: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val dateTime: Timestamp = Timestamp.now(),
    val aisleId: String = "",
    val aisleName: String = "",
    val author: User? = null,
    val histories: List<History> = emptyList()
) : Serializable