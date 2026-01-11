package xyz.graphitenerd.tassel.screens.recents

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.SmartCollection
import xyz.graphitenerd.tassel.model.SmartCollectionWithCount
import xyz.graphitenerd.tassel.screens.folders.FolderSelectionState
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.EmptyBookmarkFolder
import xyz.graphitenerd.tassel.ui.FileTree
import xyz.graphitenerd.tassel.ui.FolderBottomSheet
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.ui.SearchContent
import xyz.graphitenerd.tassel.ui.SelectionTopBar
import xyz.graphitenerd.tassel.ui.SmartCollectionsSection
import xyz.graphitenerd.tassel.ui.SwipeBox
import xyz.graphitenerd.tassel.ui.TasselSearchBar

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@Composable
fun RecentScreen(
    bookmarks: LazyPagingItems<Bookmark>,
    recentScreenState: RecentScreenState,
    folderSelectionState: FolderSelectionState,
    searchScreenState: SearchScreenState = SearchScreenState(),
    smartCollections: List<SmartCollectionWithCount> = emptyList(),
    onCollectionClick: (SmartCollection) -> Unit = {},
    onBookmarkOpen: (Bookmark) -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToAddNew: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    // Sync bottom sheet state with folder selection state
    LaunchedEffect(folderSelectionState.showFolderSelector) {
        if (folderSelectionState.showFolderSelector) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    // Handle back press to close bottom sheet
    BackHandler(enabled = bottomSheetState.isVisible) {
        scope.launch {
            bottomSheetState.hide()
            folderSelectionState.toggleFolderSelection()
        }
    }

    FolderBottomSheet(
        sheetState = bottomSheetState,
        currentFolder = folderSelectionState.currentFolder,
        selectedFolder = folderSelectionState.selectedFolder,
        onNavigateToFolder = { folderSelectionState.onClick(it) },
        onFolderSelected = { folder ->
            if (folder.folderId == folderSelectionState.selectedFolder?.folderId) {
                folderSelectionState.onFolderSelected(FileTree())
            } else {
                folderSelectionState.onFolderSelected(folder)
            }
        },
        onCreateFolder = { name ->
            folderSelectionState.createFolder(name)
        },
        onDismiss = {
            scope.launch {
                bottomSheetState.hide()
                folderSelectionState.toggleFolderSelection()
                folderSelectionState.onFolderSelected(FileTree())
            }
        },
        onConfirm = {
            folderSelectionState.updateBookmarkFolders(recentScreenState.selectedBookmarks)
            scope.launch {
                bottomSheetState.hide()
            }
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (recentScreenState.isSelectionMode) {
                    SelectionTopBar(
                        selectedCount = recentScreenState.selectedBookmarks.size,
                        onCloseSelection = { recentScreenState.clearSelectedBookmarks() },
                        onDelete = { recentScreenState.deleteBookmarks(recentScreenState.selectedBookmarks) },
                        onMoveToFolder = { folderSelectionState.toggleFolderSelection() },
                        onSelectAll = { recentScreenState.selectAllBookmarks() }
                    )
                } else {
                    HomeAppBar(
                        onClickMenuButton = onOpenDrawer,
                        onClickActionButton = onNavigateToAddNew
                    )
                }
            },
            snackbarHost = { snackbarHostState ->
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        modifier = Modifier.padding(bottom = 96.dp),
                        snackbarData = it
                    )
                }
            }
        ) {
            BackHandler(enabled = recentScreenState.isSelectionMode || searchScreenState.showSearchResults) {
                if (searchScreenState.showSearchResults) {
                    searchScreenState.toggleShowSearchResults(false)
                }
                if (recentScreenState.isSelectionMode) {
                    recentScreenState.clearSelectedBookmarks()
                }
            }

            LaunchedEffect(recentScreenState.deletedBookmarks) {
                var result: SnackbarResult? = null
                if (recentScreenState.deletedBookmarks.isNotEmpty()) {
                    result = scaffoldState.snackbarHostState.showSnackbar("Bookmark Deleted", "UNDO")
                }
                if (result == SnackbarResult.ActionPerformed) {
                    withContext(Dispatchers.IO) {
                        recentScreenState.addBookmarks(recentScreenState.deletedBookmarks)
                    }
                }
            }

            RecentScreenContent(
                bookmarks = bookmarks,
                deleteAction = { recentScreenState.deleteBookmarks(listOf(it.id)) },
                recentScreenState = recentScreenState,
                folderSelectionState = folderSelectionState,
                searchScreenState = searchScreenState,
                smartCollections = smartCollections,
                onCollectionClick = onCollectionClick,
                onBookmarkOpen = onBookmarkOpen
            )
        }
    }
}

@Composable
fun RecentScreenContent(
    bookmarks: LazyPagingItems<Bookmark>,
    deleteAction: (Bookmark) -> Unit = {},
    recentScreenState: RecentScreenState = RecentScreenState(),
    folderSelectionState: FolderSelectionState = FolderSelectionState(),
    searchScreenState: SearchScreenState = SearchScreenState(),
    smartCollections: List<SmartCollectionWithCount> = emptyList(),
    onCollectionClick: (SmartCollection) -> Unit = {},
    onBookmarkOpen: (Bookmark) -> Unit = {}
) {
//    if (bookmarks.itemCount == 0) {
//        EmptyBookmarkFolder()
//    } else if (searchScreenState.showSearchResults){
//        SearchContent(modifier = Modifier.fillMaxSize(), bookmarks = searchScreenState.searchResults)
//    } else {
//        RecentBookmarks(bookmarks, deleteAction, recentScreenState, folderSelectionState, searchScreenState)
//    }

    AnimatedContent(
        targetState = recentScreenState.screenState
    ) { targetState ->
        when (targetState) {
            ScreenState.EMPTY -> EmptyBookmarkFolder()
            ScreenState.RECENT -> RecentBookmarks(bookmarks, deleteAction, recentScreenState, folderSelectionState, searchScreenState, smartCollections, onCollectionClick, onBookmarkOpen)
            ScreenState.SEARCH -> SearchContent(modifier = Modifier.fillMaxSize(), searchScreenState = searchScreenState, bookmarks = searchScreenState.searchResults)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecentBookmarks(
    bookmarks: LazyPagingItems<Bookmark>,
    deleteAction: (Bookmark) -> Unit = {},
    recentScreenState: RecentScreenState,
    folderSelectionState: FolderSelectionState,
    searchScreenState: SearchScreenState,
    smartCollections: List<SmartCollectionWithCount> = emptyList(),
    onCollectionClick: (SmartCollection) -> Unit = {},
    onBookmarkOpen: (Bookmark) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LazyColumn(
        contentPadding = PaddingValues(20.dp, 0.dp, 20.dp, 78.dp)
    ) {

        item {

            Divider(
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(20.dp))
            TasselSearchBar(searchScreenState)
            Spacer(modifier = Modifier.height(20.dp))
            Divider(
                color = Color.Black,
            )
        }

        // Smart Collections Section - always show if we have bookmarks
        item {
            SmartCollectionsSection(
                collections = smartCollections,
                onCollectionClick = onCollectionClick,
                compactMode = true
            )
        }

        items(
            count = bookmarks.itemCount,
            key = bookmarks.itemKey { it.id },
            contentType = { "bookmark" }
        ) { index ->

            val bookmark = bookmarks[index]!!
            val isSelected = recentScreenState.selectedBookmarks.contains(bookmark.id)

            SwipeBox(
                modifier = Modifier
                    .wrapContentHeight(),
                onDelete = {
                    scope.launch(Dispatchers.IO) {
                        deleteAction(bookmark)
                    }
                },
                onEdit = {}
            ) {
                BookmarkCard(
                    bookmark = bookmark,
                    isSelected = isSelected,
                    selectionEnabled = recentScreenState.isSelectionMode,
                    onClick = {
                        if (recentScreenState.isSelectionMode) {
                            recentScreenState.onSelectBookmark(bookmark.id, isSelected)
                        } else {
                            onBookmarkOpen(bookmark)
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(bookmark.rawUrl)
                            }
                            context.startActivity(intent)
                        }
                    },
                    onLongPress = {
                        if (!recentScreenState.isSelectionMode) {
                            recentScreenState.toggleSelectionMode()
                            recentScreenState.onSelectBookmark(bookmark.id, false)
                        }
                    }
                )
                Divider(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = Color.Black,
                )
            }
        }
    }
}

@Preview
@Composable
fun previewBS() {
    val sampleBookmark = MutableList(10) {
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
