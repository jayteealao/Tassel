package xyz.graphitenerd.tassel.screens.settings

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alorma.compose.settings.ui.SettingsGroup
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import xyz.graphitenerd.tassel.screens.AuthViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val activityResultRegistry = LocalActivityResultRegistryOwner.current?.activityResultRegistry
    SettingsGroup(
        title = { Text(text = "Integration") },
    ) {
        ListItem(
            headlineContent = { Text(text = "") },
            supportingContent = {
                TextButton(
                    onClick = {
                        val signInLauncher = activityResultRegistry?.register(
                            "sign-in",
                            FirebaseAuthUIActivityResultContract(),
                        ) { res -> authViewModel.onSignInResult(res) }

                        if (FirebaseAuth.getInstance().currentUser == null) {
                            signInLauncher?.launch(authViewModel.signInIntent)
                        }
                    }
                ) {
                    Text(text = "Sign in to sync bookmarks") }
            },
            leadingContent = {
                Image(
                    Icons.Outlined.Person,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                )
            }
        )
    }
}