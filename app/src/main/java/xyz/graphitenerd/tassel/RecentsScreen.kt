package xyz.graphitenerd.tassel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkUiState
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.BottomNavButton
import xyz.graphitenerd.tassel.ui.EmptyBookmark
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.ui.SearchBar
import java.net.URL

@Composable
fun RecentsScreen(uiState: BookmarkUiState, onClickTasselButton: () -> Unit) {
    Scaffold(
        topBar = { HomeAppBar(onClickTasselButton = onClickTasselButton) },
        bottomBar = {
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
    ) {
        if (uiState.isEmpty) {
            EmptyBookmark()
        } else {
            Contents(uiState.bookmarks.collectAsLazyPagingItems())
        }
    }
}

@Composable
private fun Contents(bookmarks: LazyPagingItems<Bookmark>) {
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
            if (bookmark != null) {
                BookmarkCard(bookmark = bookmark)
            }
            Divider(
//                modifier = Modifier
//                    .width(LocalConfiguration.current.screenWidthDp.dp - 40.dp)
//                ,
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
            id = 1,
            title = "Compose layout basics  |  Jetpack Compose  |  Android Developers",
            url = URL("https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier"),
            favicon = URL(
                "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
                        "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
            )
        )
    }
    val uiState = BookmarkUiState(isEmpty = false, bookmarks = MutableStateFlow(PagingData.from(sampleBookmark)))
    RecentsScreen(uiState, {})
}