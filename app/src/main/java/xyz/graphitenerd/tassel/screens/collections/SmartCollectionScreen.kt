package xyz.graphitenerd.tassel.screens.collections

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.SmartCollection
import xyz.graphitenerd.tassel.screens.recents.RecentScreenState
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.EmptyBookmarkFolder
import xyz.graphitenerd.tassel.ui.SwipeBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SmartCollectionScreen(
    collection: SmartCollection,
    bookmarks: LazyPagingItems<Bookmark>,
    recentScreenState: RecentScreenState,
    onBackClick: () -> Unit,
    deleteAction: (Bookmark) -> Unit = {},
    onBookmarkOpen: (Bookmark) -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val context = LocalContext.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    Text(
                        text = collection.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 0.dp, 20.dp, 78.dp)
        ) {
            item {
                Divider(color = Color.Black)
                Spacer(modifier = Modifier.height(20.dp))

                // Collection description
                Text(
                    text = collection.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color.Black)
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (bookmarks.itemCount == 0) {
                item {
                    EmptyBookmarkFolder()
                }
            } else {
                items(
                    count = bookmarks.itemCount,
                    key = bookmarks.itemKey { it.rawUrl }
                ) { index ->
                    val bookmark = bookmarks[index]
                    bookmark?.let {
                        SwipeBox(
                            onSwipe = {
                                deleteAction(it)
                            }
                        ) {
                            BookmarkCard(
                                bookmark = it,
                                showCheckBox = recentScreenState.isSelectionMode,
                                isSelected = recentScreenState.selectedBookmarks.contains(it.id),
                                onCheckBoxChange = {
                                    recentScreenState.onSelectBookmark(
                                        it.id,
                                        recentScreenState.selectedBookmarks.contains(it.id)
                                    )
                                },
                                onCardLongClick = { recentScreenState.toggleSelectionMode() },
                                onCardClick = {
                                    if (recentScreenState.isSelectionMode) {
                                        recentScreenState.onSelectBookmark(
                                            it.id,
                                            recentScreenState.selectedBookmarks.contains(it.id)
                                        )
                                    } else {
                                        onBookmarkOpen(it)
                                        val browserIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(it.url ?: it.rawUrl)
                                        )
                                        context.startActivity(browserIntent)
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
