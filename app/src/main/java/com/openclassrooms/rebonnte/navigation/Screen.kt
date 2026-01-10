package com.openclassrooms.rebonnte.navigation

import kotlinx.serialization.Serializable

/**
 * Defines all navigation destinations for the app.
 * Each destination is annotated with @Serializable to enable
 * type-safe navigation using the new Navigation Compose APIs (2.7+).
 */

@Serializable
object Login

@Serializable
data class DetailMedicine(val medicineId: String)

@Serializable
data class DetailAisle(val aisleId: String)

@Serializable
object HomeAisle

@Serializable
object HomeMedicine

@Serializable
object Profile

@Serializable
object AddMedicine

@Serializable
data class EditMedicine(val medicineId: String)

@Serializable
object AddAisle