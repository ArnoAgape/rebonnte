package com.openclassrooms.rebonnte.data.dto

import com.openclassrooms.rebonnte.domain.model.Medicine
import java.io.Serializable

data class AisleDto(
    val id: String = "",
    val name: String = "",
    val medicines: List<Medicine> = emptyList()
) : Serializable