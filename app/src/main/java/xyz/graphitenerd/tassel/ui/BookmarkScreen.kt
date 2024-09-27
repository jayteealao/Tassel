package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.screens.recents.BookmarkViewModel

@Composable
fun BookmarkScreen(
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        bookmarks.forEach { bookmark ->
            BookmarkCardWithTags(bookmark, bookmarkViewModel)
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
        }
    }
}

@Composable
fun BookmarkCardWithTags(bookmark: Bookmark, bookmarkViewModel: BookmarkViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = bookmark.title ?: "No Title", style = MaterialTheme.typography.h6)
            Text(text = bookmark.rawUrl, style = MaterialTheme.typography.body2)

            Spacer(modifier = Modifier.height(8.dp))

            TagManager(
                tags = bookmark.tags,
                onAddTag = { tag -> bookmarkViewModel.addTagToBookmark(bookmark.id, tag) },
                onRemoveTag = { tag -> bookmarkViewModel.removeTagFromBookmark(bookmark.id, tag) }
            )
        }
    }
}

