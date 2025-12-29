package com.openclassrooms.rebonnte.domain.model

import java.io.Serializable

class Aisle(
    var id: String = "",
    var name: String = "",
    var medicines: List<Medicine>
) : Serializable