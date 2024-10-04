package xyz.graphitenerd.tassel.service

interface AccountService {
    fun hasUser(): Boolean
    fun isAnonymousUser(): Boolean
    fun getUserId(): String
    fun getUserDetails(): UserDetails
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun sendRecoveryEmail(email: String, onResult: (Throwable?) -> Unit)
    fun createAnonymousAccount(onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun deleteAccount(onResult: (Throwable?) -> Unit)
    fun signOut()
}

data class UserDetails(
    val userId: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
)