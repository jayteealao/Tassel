package xyz.graphitenerd.tassel

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import okhttp3.internal.toImmutableList
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkUiState
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.BottomNavButton
import xyz.graphitenerd.tassel.ui.EmptyBookmark
import xyz.graphitenerd.tassel.ui.SearchBar

@Composable
fun RecentsScreen(bookmarksFlow: Flow<List<Bookmark>>) {
    val bookmarks: State<List<Bookmark>> = bookmarksFlow.collectAsState(emptyList<Bookmark>())
    if (bookmarks.value.isEmpty()) {
        EmptyBookmark()
    } else {
        Contents(bookmarks.value)
    }
}

@Composable
fun RecentsScreen(uiState: List<Bookmark>) {
    if (uiState.isEmpty()) {
        EmptyBookmark()
    } else {
        Contents(uiState)
    }
}

@Composable
private fun BottomNavBar(uiState: BookmarkUiState) {
    Box(
        modifier = Modifier
            .background(color = Color(0xff, 0xff, 0xff, 128))
            .fillMaxWidth()
            .height(72.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        BottomNavButton(state = uiState.bottomNavBarState)
    }
}


@Composable
private fun Contents(bookmarks: List<Bookmark>) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp,0.dp,20.dp,72.dp)
    ) {
        item {

            Divider(
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(20.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(20.dp))
            Divider(
                color = Color.Black,
            )
        }
        items(bookmarks) { bookmark ->
            Log.e("tassel", "in recent screen column, current bookmark $bookmark")
            BookmarkCard(bookmark = bookmark)
            Divider(
                color = Color.Black,
            )
        }
    }
}

@Preview
@Composable
fun previewBS() {
    val sampleBookmark = MutableList(10){
        Bookmark(
            title = "Compose layout basics  |  Jetpack Compose  |  Android Developers",
            rawUrl = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier",
            favIcon = "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
                        "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
        )
    }
//    val uiState = BookmarkUiState(isEmpty = false, bookmarks = MutableStateFlow(PagingData.from(sampleBookmark)))
//    RecentsScreen(uiState)
}