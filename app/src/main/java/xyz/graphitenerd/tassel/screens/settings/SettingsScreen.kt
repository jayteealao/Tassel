package xyz.graphitenerd.tassel.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import xyz.graphitenerd.tassel.screens.AuthViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onSignInClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val userDetails by authViewModel.userDetails.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Account Section
        SettingsSectionHeader(title = "ACCOUNT")
        
        SettingsItem(
            icon = Icons.Outlined.Person,
            title = if (currentUser != null) {
                userDetails?.displayName ?: currentUser.email ?: "Signed In"
            } else {
                "Not signed in"
            },
            subtitle = if (currentUser != null) {
                "Tap to manage account"
            } else {
                "Sign in to sync bookmarks"
            },
            onClick = onSignInClick,
            showArrow = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // About Section
        SettingsSectionHeader(title = "ABOUT")
        
        SettingsItem(
            icon = Icons.Outlined.Info,
            title = "Version",
            subtitle = "1.0.0",
            onClick = {},
            showArrow = false
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
    Divider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = Color.Black
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showArrow: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    Divider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = Color.Black
    )
}