package xyz.graphitenerd.tassel.model

import android.app.Activity.RESULT_OK
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.Field
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.model.service.AccountService
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val accountService: AccountService,
): ViewModel() {

    lateinit var user: FirebaseUser

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                user = firebaseUser
            }
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.GreenTheme)
            .setLogo(R.drawable.tassel_app_icon)
            .build()
        // [END auth_fui_create_intent]

}

data class SignInForm(
    val email: Field<SignInForm, String>,
    val password: Field<SignInForm, String>,
)