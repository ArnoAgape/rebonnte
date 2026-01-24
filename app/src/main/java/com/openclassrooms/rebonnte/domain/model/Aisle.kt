package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.data.dto.AisleDto

/**
 * Domain model representing an aisle.
 *
 * Used within the application business logic and UI.
 */
data class Aisle(
    val id: String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val medicines: List<Medicine> = emptyList()
) {
    /**
     * Converts this domain model to its Firestore DTO representation.
     */
    fun toDto(): AisleDto {
        return AisleDto(
            id = id,
            name = name,
            nameLowercase = nameLowercase,
            createdAt = createdAt,
            medicines = medicines
        )
    }

    /**
     * Converts a Firestore DTO to a domain-level [Aisle].
     */
    companion object {
        fun fromDto(dto: AisleDto): Aisle {
            return Aisle(
                id = dto.id,
                name = dto.name,
                nameLowercase = dto.nameLowercase,
                createdAt = dto.createdAt,
                medicines = dto.medicines
            )
        }
    }
}