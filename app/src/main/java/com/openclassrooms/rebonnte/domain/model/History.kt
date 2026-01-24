package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.data.dto.HistoryDto
import java.time.Instant

/**
 * Domain model representing a history.
 *
 * Used within the application business logic and UI.
 */
data class History(
    val id: String = "",
    val medicineName: String = "",
    val author: User? = null,
    val dateTime: Instant = Instant.now(),
    val quantity: Int = 0,
    val changeType: StockChangeType = StockChangeType.ADDED
) {
    /**
     * Converts this domain model to its Firestore DTO representation.
     */
    fun toDto(): HistoryDto {
        return HistoryDto(
            id = id,
            medicineName = medicineName,
            author = author?.toDto(),
            dateTime = Timestamp(dateTime.epochSecond, dateTime.nano),
            quantity = quantity,
            changeType = changeType
        )
    }

    /**
     * Converts a Firestore DTO to a domain-level [History].
     */
    companion object {
        fun fromDto(dto: HistoryDto): History {
            return History(
                id = dto.id,
                medicineName = dto.medicineName,
                author = dto.author?.let { User.fromDto(it) },
                dateTime = dto.dateTime.toDate().toInstant(),
                quantity = dto.quantity,
                changeType = dto.changeType
            )
        }
    }
}