package xyz.graphitenerd.tassel.ui

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FolderCard(folder: BookmarkFolder, onClick: () -> Unit) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(),
        elevation = 0.dp,
        shape = RectangleShape
    ) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
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
