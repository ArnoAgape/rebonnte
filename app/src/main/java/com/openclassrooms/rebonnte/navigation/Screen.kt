package com.openclassrooms.rebonnte.navigation

import kotlinx.serialization.Serializable

/**
 * Defines all navigation destinations for the app.
 * Each destination is annotated with @Serializable to enable
 * type-safe navigation using the new Navigation Compose APIs (2.7+).
 */
@Serializable
data class DetailMedicine(val medicineId: String)

@Serializable
data class DetailAisle(val aisleId: String)

@Serializable
object AisleRoute

@Serializable
object MedicineRoute