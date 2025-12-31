package com.openclassrooms.rebonnte.data.dto

import java.io.Serializable

class HistoryDto(
    val medicineName: String = "",
    val userId: String = "",
    val date: String = "",
    val details: String = ""
) : Serializable