package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.screens.recents.BookmarkViewModel

@Composable
fun TagManager(
    bookmark: Bookmark,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    var newTag by remember { mutableStateOf(TextFieldValue("")) }
    val tags = remember { mutableStateListOf(*bookmark.tags.toTypedArray()) }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Tags", style = MaterialTheme.typography.h6)

        Row(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = newTag,
                onValueChange = { newTag = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray)
                    .padding(8.dp)
            )
            Button(
                onClick = {
                    if (newTag.text.isNotBlank()) {
                        viewModel.addTagToBookmark(bookmark.id, newTag.text)
                        tags.add(newTag.text)
                        newTag = TextFieldValue("")
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add Tag")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        tags.forEach { tag ->
            TagItem(tag = tag, onRemove = {
                viewModel.removeTagFromBookmark(bookmark.id, tag)
                tags.remove(tag)
            })
        }
    }
}

@Composable
fun TagItem(tag: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onRemove() }
            .background(Color.Gray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = tag, color = Color.White)
        Text(text = "Remove", color = Color.Red)
    }
}

