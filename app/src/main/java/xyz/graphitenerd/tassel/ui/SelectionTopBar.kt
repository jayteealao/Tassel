package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onCloseSelection: () -> Unit,
    onDelete: () -> Unit,
    onMoveToFolder: () -> Unit,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Close button
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCloseSelection) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close selection",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Center: Selected count
            Text(
                text = "$selectedCount selected",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        // Right: Action buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onSelectAll) {
                Icon(
                    imageVector = Icons.Outlined.SelectAll,
                    contentDescription = "Select all",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onMoveToFolder) {
                Icon(
                    imageVector = Icons.Outlined.FolderOpen,
                    contentDescription = "Move to folder",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    Divider(color = Color.Black)
}
