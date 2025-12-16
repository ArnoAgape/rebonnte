package com.openclassrooms.rebonnte.ui.screen.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.openclassrooms.rebonnte.ui.common.Event
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.openclassrooms.rebonnte.R

/**
 * Creates and remembers an email sign-up launcher using FirebaseUI Auth.
 * On success, the user is synced with Firestore and a success message is sent.
 * On failure, an error event is emitted.
 *
 * @param loginViewModel ViewModel used to sync the user and send UI events.
 * @return A lambda that launches the email sign-up flow.
 */
@Composable
fun authSignInLauncher(
    loginViewModel: LoginViewModel
): () -> Unit {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = IdpResponse.fromResultIntent(result.data)

        if (result.resultCode == Activity.RESULT_OK) {
            loginViewModel.syncUserWithFirestore()
            loginViewModel.sendEvent(Event.ShowMessage(R.string.success_sign_up))

        } else {
            if (response == null) {
                loginViewModel.sendEvent(Event.ShowMessage(R.string.error_sign_up))
            }
        }
    }

    val providers = remember { listOf(AuthUI.IdpConfig.EmailBuilder().build()) }
    val signInIntent = remember(providers) {
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.Theme_Rebonnte)
            .setAvailableProviders(providers)
            .build()
    }

    return {
        launcher.launch(signInIntent)
    }
}