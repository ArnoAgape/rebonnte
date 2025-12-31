package com.openclassrooms.rebonnte.domain.model

import com.openclassrooms.rebonnte.data.dto.HistoryDto

class History(
    val medicineName: String = "",
    val userId: String = "",
    val date: String = "",
    val details: String = ""
) {
    fun toDto(): HistoryDto {
        return HistoryDto(
            medicineName = medicineName,
            userId = userId,
            date = date,
            details = details
        )
    }

    companion object {
        fun fromDto(dto: HistoryDto): History {
            return History(
                medicineName = dto.medicineName,
                userId = dto.userId,
                date = dto.date,
                details = dto.details
            )
        }
    }
}