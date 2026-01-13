package com.openclassrooms.rebonnte.data.dto

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.StockChangeType
import java.io.Serializable

data class HistoryDto(
    val id: String = "",
    val medicineName: String = "",
    val author: UserDto? = null,
    val dateTime: Timestamp = Timestamp.now(),
    val quantity: Int = 0,
    val changeType: StockChangeType = StockChangeType.ADDED
) : Serializable