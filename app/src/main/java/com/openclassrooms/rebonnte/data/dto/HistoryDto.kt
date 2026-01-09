package com.openclassrooms.rebonnte.data.dto

import com.google.firebase.Timestamp
import java.io.Serializable

data class HistoryDto(
    val id: String = "",
    val medicineName: String = "",
    val author: UserDto? = null,
    val dateTime: Timestamp = Timestamp.now(),
    val details: String = ""
) : Serializable