package com.openclassrooms.rebonnte.domain.model

import com.openclassrooms.rebonnte.data.dto.UserDto

/**
 * Domain model representing a user.
 *
 * Used within the application business logic and UI.
 */
data class User(
    val id: String = "",
    val displayName: String? = "",
    val phoneNumber: String? = "",
    val email: String? = ""
) {
    /**
     * Converts this domain model to its Firestore DTO representation.
     */
    fun toDto(): UserDto {
        return UserDto(
            id = id,
            displayName = displayName,
            phoneNumber = phoneNumber,
            email = email
        )
    }

    /**
     * Converts a Firestore DTO to a domain-level [User].
     */
    companion object {
        fun fromDto(dto: UserDto): User {
            return User(
                id = dto.id,
                displayName = dto.displayName,
                phoneNumber = dto.phoneNumber,
                email = dto.email
            )
        }
    }
}