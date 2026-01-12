package xyz.graphitenerd.tassel.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Folder
import xyz.graphitenerd.tassel.model.BookmarkFolder

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun FolderCard(
    folder: BookmarkFolder,
    onClick: () -> Unit = {},
    isSelected: Boolean = false,
    selectionEnabled: Boolean = false,
    onLongPress: () -> Unit = {}
) {
    Card(
        onClick = {
            if (selectionEnabled) {
                onLongPress()
            } else {
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight()
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.Black, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
        elevation = 0.dp,
        shape = RectangleShape
    ) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Selection checkbox
                AnimatedVisibility(
                    visible = selectionEnabled,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    SelectionCheckbox(
                        isSelected = isSelected,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Icon(
                    imageVector = FontAwesomeIcons.Solid.Folder,
                    contentDescription = "folder icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                )
                Text(
                    text = folder.name.uppercase(),
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                )
            }
            Divider(
                modifier = Modifier.padding(horizontal = 2.dp),
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun previewSelectFolder() {
    FolderCard(folder = BookmarkFolder(id = 1, name = "home", parentId = null), onClick = {})
}
