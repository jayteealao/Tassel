package xyz.graphitenerd.tassel.screens

import android.app.Activity.RESULT_OK
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.Field
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import xyz.graphitenerd.tassel.service.UserDetails
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
): ViewModel() {

//    lateinit var user: FirebaseUser

    private var _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?>
        get() = _user

    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?>
        get() = _userDetails


    fun hasUser() = accountService.hasUser()

    fun signOut() {
        accountService.signOut()
        _user.value = null
        _userDetails.value = null
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                _user.value = firebaseUser
                _userDetails.value = accountService.getUserDetails()

                // Initialize Firebase sync with the authenticated user's ID
                initializeFirebaseSync()
            }
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    /**
     * Initializes Firebase sync by setting the user ID in StorageService.
     * This ensures that bookmarks and folders sync to the correct user's
     * Firestore collection after successful authentication.
     */
    private fun initializeFirebaseSync() {
        if (accountService.hasUser() && !storageService.isUserSet()) {
            val userId = accountService.getUserId()
            if (userId.isNotEmpty()) {
                storageService.setUserId(userId)
            }
        }
    }

    init {
        _userDetails.value = accountService.getUserDetails()
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