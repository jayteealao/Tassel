package xyz.graphitenerd.tassel.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.screens.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit = {},
    onSignInSuccess: () -> Unit = {}
) {
    var isSignedIn by remember { mutableStateOf(authViewModel.hasUser()) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result ->
        authViewModel.onSignInResult(result)
        isSignedIn = authViewModel.hasUser()
        if (isSignedIn) {
            onSignInSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isSignedIn) "Account" else "Sign In",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black,
            elevation = 0.dp
        )

        Divider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.Black
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tassel_app_icon),
                contentDescription = "Tassel Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Tassel",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to sync your bookmarks across devices",
                style = MaterialTheme.typography.body1,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (isSignedIn && currentUser != null) {
                SignedInContent(
                    displayName = currentUser.displayName ?: currentUser.email ?: "User",
                    email = currentUser.email ?: "",
                    onSignOut = {
                        authViewModel.signOut()
                        isSignedIn = false
                    }
                )
            } else {
                SignInButtons(
                    onSignIn = {
                        signInLauncher.launch(authViewModel.signInIntent)
                    }
                )
            }
        }
    }
}

@Composable
private fun SignInButtons(
    onSignIn: () -> Unit
) {
    Button(
        onClick = onSignIn,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text(
            text = "Sign in with Google",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedButton(
        onClick = onSignIn,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = "Sign in with Email",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = "By signing in, you agree to sync your bookmarks securely with Firebase",
        style = MaterialTheme.typography.caption,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun SignedInContent(
    displayName: String,
    email: String,
    onSignOut: () -> Unit
) {
    Text(
        text = "Signed in as",
        style = MaterialTheme.typography.body2,
        color = Color.Gray
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = displayName,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

    Text(
        text = email,
        style = MaterialTheme.typography.body2,
        color = Color.Gray
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = onSignOut,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = "Sign Out",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInButtonsPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SignInButtons(
            onSignIn = {}
        )
    }
}
