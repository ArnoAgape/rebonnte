package com.openclassrooms.rebonnte.data.dto

import java.io.Serializable

data class UserDto(
    val id: String = "",
    val displayName: String? = "",
    val phoneNumber: String? = "",
    val email: String? = ""
) : Serializable