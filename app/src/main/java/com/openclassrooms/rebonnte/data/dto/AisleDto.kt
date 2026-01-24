package com.openclassrooms.rebonnte.data.dto

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.Medicine
import java.io.Serializable

/**
 * Data Transfer Object representing an aisle stored in Firestore.
 *
 * Used for persistence and network transport.
 */
data class AisleDto(
    val id: String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val medicines: List<Medicine> = emptyList()
) : Serializable