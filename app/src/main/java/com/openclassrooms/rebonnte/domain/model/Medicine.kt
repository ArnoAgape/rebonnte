package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.data.dto.MedicineDto
import java.time.Instant

/**
 * Domain model representing a medicine.
 *
 * Used within the application business logic and UI.
 */
data class Medicine(
    val id : String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val stock: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val dateTime: Instant = Instant.now(),
    val aisleId: String = "",
    val aisleName: String = "",
    val author: User? = null,
    val histories: List<History> = emptyList()
) {
    /**
     * Converts this domain model to its Firestore DTO representation.
     */
    fun toDto(): MedicineDto {
        return MedicineDto(
            id = id,
            name = name,
            nameLowercase = nameLowercase,
            stock = stock,
            createdAt = createdAt,
            dateTime = Timestamp(dateTime.epochSecond, dateTime.nano),
            aisleId = aisleId,
            aisleName = aisleName,
            author = author,
            histories = histories
        )
    }

    /**
     * Converts a Firestore DTO to a domain-level [Medicine].
     */
    companion object {
        fun fromDto(dto: MedicineDto): Medicine {
            return Medicine(
                id = dto.id,
                name = dto.name,
                nameLowercase = dto.nameLowercase,
                stock = dto.stock,
                createdAt = dto.createdAt,
                dateTime = dto.dateTime.toDate().toInstant(),
                aisleId = dto.aisleId,
                aisleName = dto.aisleName,
                author = dto.author,
                histories = dto.histories
            )
        }
    }
}