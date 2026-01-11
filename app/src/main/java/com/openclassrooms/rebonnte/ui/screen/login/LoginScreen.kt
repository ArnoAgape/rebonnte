package com.openclassrooms.rebonnte.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (isSignedIn) {

                null, true -> {
                    // Auth state still loading
                    CircularProgressIndicator()
                }

                false -> {
                    // User not signed in
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        // Logo
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Logo of Rebonnté",
                            modifier = Modifier
                                .size(160.dp)
                                .padding(bottom = 16.dp)
                        )

                        // Title
                        Text(
                            text = stringResource(R.string.auth_check_title),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )

                        // Sub-title
                        Text(
                            text = stringResource(R.string.auth_check_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Button
                        Button(
                            onClick = onLaunchAuth,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text(stringResource(R.string.auth_check_button))
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginScreenPreview() {
    RebonnteTheme {
        LoginScreen(
            onLaunchAuth = {},
            isSignedIn = false,
            onLoginSuccess = {}
        )
    }
}