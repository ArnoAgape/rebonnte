package com.openclassrooms.rebonnte.data.dto

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.Medicine
import java.io.Serializable

data class AisleDto(
    val id: String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val medicines: List<Medicine> = emptyList()
) : Serializable