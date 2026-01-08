package com.openclassrooms.rebonnte.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onLaunchAuth: () -> Unit,
    isSignedIn: Boolean?,
    onLoginSuccess: () -> Unit
) {
    // Navigation after login success
    LaunchedEffect(isSignedIn) {
        if (isSignedIn == true) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (isSignedIn) {

            null -> {
                // Auth state still loading
                CircularProgressIndicator()
            }

            false -> {
                // User not signed in → explicit action
                Button(onClick = onLaunchAuth) {
                    Text("Se connecter")
                }
            }

            true -> {
                // Brief loading while navigation happens
                CircularProgressIndicator()
            }
        }
    }
}