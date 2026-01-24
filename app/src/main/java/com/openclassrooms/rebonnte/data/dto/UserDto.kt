package com.openclassrooms.rebonnte.data.dto

import java.io.Serializable

/**
 * Data Transfer Object representing an aisle stored in Firestore.
 *
 * Used for persistence and network transport.
 */
data class UserDto(
    val id: String = "",
    val displayName: String? = "",
    val phoneNumber: String? = "",
    val email: String? = ""
) : Serializable