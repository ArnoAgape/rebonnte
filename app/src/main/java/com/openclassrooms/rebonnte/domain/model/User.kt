package com.openclassrooms.rebonnte.domain.model

import com.openclassrooms.rebonnte.data.dto.UserDto

data class User(
    val id: String = "",
    val displayName: String? = "",
    val phoneNumber: String? = "",
    val email: String? = ""
) {
    fun toDto(): UserDto {
        return UserDto(
            id = id,
            displayName = displayName,
            phoneNumber = phoneNumber,
            email = email
        )
    }

    companion object {
        fun fromDto(dto: UserDto): User {
            return User(
                id = dto.id,
                displayName = dto.displayName,
                phoneNumber = dto.phoneNumber,
                email = dto.email
            )
        }
    }
}