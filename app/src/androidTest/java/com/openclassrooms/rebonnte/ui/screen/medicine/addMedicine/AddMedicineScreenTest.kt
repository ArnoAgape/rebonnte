package com.openclassrooms.rebonnte.ui.screen.medicine.addMedicine

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.R
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class AddMedicineScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysCorrectAddMedicineContent() {

        val fakeAisles = listOf(
            Aisle(id = "1", name = "Paracetamol"),
            Aisle(id = "2", name = "Syrup"),
            Aisle(id = "3", name = "Cream")
        )

        composeTestRule.setContent {

            AddMedicineContent(
                dateTime = Instant.EPOCH,
                onDateTimeChange = {},
                numberOfMedicines = 3,
                onNumberOfMedicinesChange = {},
                nameMedicine = "Paracetamol",
                onNameMedicineChanged = {},
                aisles = fakeAisles,
                selectedAisle = fakeAisles.first(),
                onAisleSelected = {},
                onSaveClicked = {},
                isMedicineValid = true,
                isLoading = false
            )
        }

        val receptionDate = composeTestRule.activity.getString(
            R.string.hint_datetime
        )
        val name = composeTestRule.activity.getString(
            R.string.hint_medicine_name
        )
        val aisle = composeTestRule.activity.getString(
            R.string.aisle
        )
        val stock = composeTestRule.activity.getString(
            R.string.hint_number_of_medicines
        )
        val addMedicineButton = composeTestRule.activity.getString(R.string.action_add)

        composeTestRule.onNodeWithText(receptionDate).assertIsDisplayed()
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
        composeTestRule.onNodeWithText(aisle).assertIsDisplayed()
        composeTestRule.onNodeWithText(stock).assertIsDisplayed()

        composeTestRule.onNodeWithTag(addMedicineButton).performClick()
    }
}