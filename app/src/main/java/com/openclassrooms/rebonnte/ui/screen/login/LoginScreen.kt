package com.openclassrooms.rebonnte.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onLaunchAuth: () -> Unit,
    isSignedIn: Boolean
) {
    // Prevent multiple reloading
    var hasLaunched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isSignedIn) {
        if (!isSignedIn && !hasLaunched) {
            hasLaunched = true
            onLaunchAuth()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
