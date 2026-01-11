package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.graphitenerd.tassel.R

@Composable
fun NavDrawerContent(
    onSettingsClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onCloseDrawer: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
    ) {
        // Header with logo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.tassel_app_icon),
                contentDescription = "Tassel Logo",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tassels",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Divider(color = Color.Black, thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Menu items
        DrawerMenuItem(
            icon = Icons.Outlined.Settings,
            label = "Settings",
            onClick = {
                onCloseDrawer()
                onSettingsClick()
            }
        )

        DrawerMenuItem(
            icon = Icons.Outlined.Info,
            label = "About",
            onClick = {
                onCloseDrawer()
                onAboutClick()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Divider(color = Color.Black, thickness = 1.dp)
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.caption,
            color = Color.Gray,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = Color.Black
        )
    }
}
