package com.openclassrooms.rebonnte.domain.model

import java.io.Serializable

data class Medicine(
    var name: String,
    var stock: Int,
    var nameAisle: String,
    var histories: List<History>
) : Serializable