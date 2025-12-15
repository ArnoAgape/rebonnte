package com.openclassrooms.rebonnte.domain.model

import java.io.Serializable

class History(
    var medicineName: String,
    var userId: String,
    var date: String,
    var details: String
) : Serializable