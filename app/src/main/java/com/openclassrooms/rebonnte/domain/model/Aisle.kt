package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.data.dto.AisleDto

data class Aisle(
    val id: String = "",
    val name: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val medicines: List<Medicine> = emptyList()
) {
    fun toDto(): AisleDto {
        return AisleDto(
            id = id,
            name = name,
            createdAt = createdAt,
            medicines = medicines
        )
    }

    companion object {
        fun fromDto(dto: AisleDto): Aisle {
            return Aisle(
                id = dto.id,
                name = dto.name,
                createdAt = dto.createdAt,
                medicines = dto.medicines
            )
        }
    }
}