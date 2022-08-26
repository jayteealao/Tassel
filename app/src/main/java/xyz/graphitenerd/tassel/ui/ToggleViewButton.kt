package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Folder
import compose.icons.fontawesomeicons.solid.History

@Composable
fun IconButton(icon: ImageVector, onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(48.dp)
            .background(Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        colors = buttonColors(backgroundColor = Color.Black)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun TextButton(onClick: () -> Unit, text: @Composable () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(48.dp)
            .background(Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        colors = buttonColors(backgroundColor = Color.White),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        text()
    }
}

@Composable
fun ToggleButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    selected: Boolean
) {
//    var toggle by remember { mutableStateOf(selected) }

    when (selected) {
        true -> {
            IconButton(
                icon = icon,
                onClick = {}
            )
        }
        false -> {
            TextButton(
                onClick = onClick
            ) {
                Text(text = text)
            }
        }
    }
}

enum class ToggleButtonState {
    FOLDERS,
    RECENTS
}

@Composable
fun BottomNavButton(state: ToggleButtonState, onClick: (state: ToggleButtonState) -> Unit = {}) {

    var toggleButtonState by remember { mutableStateOf(state) }

    Row(modifier = Modifier.wrapContentSize()) {

        ToggleButton(
            icon = FontAwesomeIcons.Solid.History,
            text = "Recents",
            onClick = {
                toggleButtonState = ToggleButtonState.RECENTS
                onClick(toggleButtonState)
            },
            selected = toggleButtonState == ToggleButtonState.RECENTS
        )

        ToggleButton(
            icon = FontAwesomeIcons.Solid.Folder,
            text = "Folders",
            onClick = {
                toggleButtonState = ToggleButtonState.FOLDERS
                onClick(toggleButtonState)
            },
            selected = toggleButtonState == ToggleButtonState.FOLDERS
        )
    }
}

@Preview
@Composable
fun prevIB() {
    IconButton(icon = Icons.Default.Info, onClick = {})
}

@Preview
@Composable
fun prevTB() {
    TextButton(onClick = {},) {
        Text(text = "Folders")
    }
}

@Preview
@Composable
fun prevToggle() {

    var toggle by remember { mutableStateOf(false) }

    ToggleButton(
        icon = Icons.Default.Email,
        text = "Folder",
        onClick = {
            toggle = !toggle
        },
        selected = toggle == true
    )
}

@Preview
@Composable
fun prevBNB() {
    BottomNavButton(ToggleButtonState.RECENTS)
}
