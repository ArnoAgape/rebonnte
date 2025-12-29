package com.openclassrooms.rebonnte.data.dto

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.User
import java.io.Serializable

data class MedicineDto(
    var id : String = "",
    var name: String = "",
    var stock: Int = 0,
    val dateTime: Timestamp = Timestamp.now(),
    var nameAisle: String = "",
    var author: User? = null,
    var histories: List<History> = emptyList()
) : Serializable