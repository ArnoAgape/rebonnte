package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.data.dto.HistoryDto
import java.time.Instant

data class History(
    val medicineName: String = "",
    val author: User? = null,
    val dateTime: Instant = Instant.now(),
    val details: String = ""
) {
    fun toDto(): HistoryDto {
        return HistoryDto(
            medicineName = medicineName,
            author = author?.toDto(),
            dateTime = Timestamp(dateTime.epochSecond, dateTime.nano),
            details = details
        )
    }

    companion object {
        fun fromDto(dto: HistoryDto): History {
            return History(
                medicineName = dto.medicineName,
                author = dto.author?.let { User.fromDto(it) },
                dateTime = dto.dateTime.toDate().toInstant(),
                details = dto.details
            )
        }
    }
}