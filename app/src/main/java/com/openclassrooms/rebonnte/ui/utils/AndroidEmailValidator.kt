package com.openclassrooms.rebonnte.ui.utils

import android.util.Patterns
import javax.inject.Inject
import kotlin.text.isNullOrBlank

/**
 * Utility class used to validate email addresses using Android's regex patterns.
 */
class AndroidEmailValidator @Inject constructor() {
    fun validate(email: String?): Boolean {
        return !email.isNullOrBlank()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}