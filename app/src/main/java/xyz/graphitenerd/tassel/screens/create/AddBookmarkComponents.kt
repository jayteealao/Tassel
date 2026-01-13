package xyz.graphitenerd.tassel.screens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Font
import compose.icons.fontawesomeicons.solid.Folder
import compose.icons.fontawesomeicons.solid.Link
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.FileTree

@Composable
fun UrlFieldSection(
    value: String,
    isLoading: Boolean,
    error: String?,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.body1,
            label = { Text("URL") },
            leadingIcon = {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Link,
                    contentDescription = "URL",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Black,
                focusedBorderColor = Color.Black,
                backgroundColor = Color.White
            )
        )

        if (isLoading) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Fetching title & preview...",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        } else if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TitleFieldSection(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.body1,
        label = { Text("Title (optional)") },
        leadingIcon = {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Font,
                contentDescription = "Title",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Black,
            backgroundColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FolderSelectorRow(
    selectedFolder: FileTree?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.Black),
        backgroundColor = Color.White,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Folder,
                    contentDescription = "Folder",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = selectedFolder?.folderName ?: "Choose folder",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun BookmarkPreview(
    show: Boolean,
    bookmark: Bookmark?,
    modifier: Modifier = Modifier
) {
    if (show && bookmark != null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.background(Color.White)
        ) {
            BookmarkCard(bookmark = bookmark)
            Divider(color = Color.Black)
        }
    }
}

@Composable
fun PreviewSection(
    isLoading: Boolean,
    bookmark: Bookmark?
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Loading preview...",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
                Divider(color = Color.Black)
            }
            bookmark != null -> {
                BookmarkCard(bookmark = bookmark)
                Divider(color = Color.Black)
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Preview will appear after entering a URL",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun PrimaryActionBar(
    isSaving: Boolean,
    isEnabled: Boolean,
    saveError: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        if (!saveError.isNullOrBlank()) {
            Text(
                text = saveError,
                style = MaterialTheme.typography.caption,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        Button(
            onClick = onClick,
            enabled = isEnabled && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White,
                disabledBackgroundColor = Color.LightGray,
                disabledContentColor = Color.White
            ),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = if (isSaving) "Saving..." else "Save bookmark",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
