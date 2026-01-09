package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.data.dto.MedicineDto
import java.time.Instant

data class Medicine(
    val id : String = "",
    val name: String = "",
    val stock: Int = 0,
    val dateTime: Instant = Instant.now(),
    val aisleId: String = "",
    val aisleName: String = "",
    val author: User? = null,
    val histories: List<History> = emptyList()
) {
    fun toDto(): MedicineDto {
        return MedicineDto(
            id = id,
            name = name,
            stock = stock,
            dateTime = Timestamp(dateTime.epochSecond, dateTime.nano),
            aisleId = aisleId,
            aisleName = aisleName,
            author = author,
            histories = histories
        )
    }

    companion object {
        fun fromDto(dto: MedicineDto): Medicine {
            return Medicine(
                id = dto.id,
                name = dto.name,
                stock = dto.stock,
                dateTime = dto.dateTime.toDate().toInstant(),
                aisleId = dto.aisleId,
                aisleName = dto.aisleName,
                author = dto.author,
                histories = dto.histories
            )
        }
    }
}