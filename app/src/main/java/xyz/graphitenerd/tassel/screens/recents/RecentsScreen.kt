package xyz.graphitenerd.tassel.screens.recents

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.screens.Screens
import xyz.graphitenerd.tassel.screens.folders.FolderSelectionState
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.EmptyBookmarkFolder
import xyz.graphitenerd.tassel.ui.FileTree
import xyz.graphitenerd.tassel.ui.FolderSelectorCard
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.ui.SearchBar
import xyz.graphitenerd.tassel.ui.SwipeBox

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@Composable
fun RecentScreen(
    bookmarks: LazyPagingItems<Bookmark>,
    recentScreenState: RecentScreenState,
    folderSelectionState: FolderSelectionState,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavController, //TODO: remove navcontroller
    onNavigateToAddNew: () -> Unit = {}, //TODO: change to onNavigate with a nullable Long if null, navigate to add new
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeAppBar(
                onClickMenuButton = {
                    navController.navigate(Screens.SETTINGS.name)
                },
                onClickActionButton = onNavigateToAddNew
            )
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


        BackHandler(enabled = recentScreenState.isSelectionMode) {
//            selectionState.toggleSelectionMode()
            recentScreenState.clearSelectedBookmarks()
        }

//        LaunchedEffect(true) {
//            bookmarkViewModel.syncBookmarksToCloud() // TODO, fix sync to cloud
//        }

//        TODO: if a bookmark deletion is undone, trying to undelete it again doesnt show the snackbar
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
            navController = navController,
            deleteAction = { recentScreenState.deleteBookmarks(listOf(it.id)) },
            recentScreenState = recentScreenState,
            folderSelectionState = folderSelectionState
        )

        AnimatedVisibility(
            folderSelectionState.showFolderSelector
        ) {
            var width = 0.dp
            val scrollState = rememberScrollState()
            Column {
                Dialog (
                    onDismissRequest = { folderSelectionState.toggleFolderSelection() },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.height(384.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    ){
                        val maxWidth = maxWidth
                        Column(
                            modifier = Modifier
                                .heightIn(max = 320.dp)
                                .verticalScroll(scrollState)
                                .fillMaxWidth()

                        ) {
                            FolderSelectorCard(folderSelectionState.currentFolder, isHeader = true,
                                onNavigate = { folderSelectionState.onClick(it) }
                            )
                            HorizontalDivider()
                            folderSelectionState.currentFolder.children.forEach { child ->
                                val isSelected =
                                    child.folderId == folderSelectionState.selectedFolder?.folderId
                                FolderSelectorCard(
                                    child,
                                    isSelected = isSelected,
                                    onNavigate = { folderSelectionState.onClick(it) },
                                ) {
                                    if (!isSelected) {
                                        folderSelectionState.onFolderSelected(child)
                                    } else {
                                        folderSelectionState.onFolderSelected(FileTree())
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                modifier = Modifier.height(48.dp).width((maxWidth / 2) - 12.dp),
                                onClick = {
                                    folderSelectionState.toggleFolderSelection()
                                    folderSelectionState.onFolderSelected(FileTree())
                                },
                                colors = ButtonDefaults.outlinedButtonColors()
                                    .copy(
                                        containerColor = Color.DarkGray,
                                        contentColor = Color.White,

                                    )
                            ) {
                                Text(text = "Cancel")
                            }
                            OutlinedButton(
                                modifier = Modifier.height(48.dp).width((maxWidth / 2) - 12.dp),
                                enabled = folderSelectionState.selectedFolder != null,
                                onClick = {
                                    folderSelectionState.updateBookmarkFolders(recentScreenState.selectedBookmarks)
//                                    folderSelectionState.toggleFolderSelection()
                                },
                                colors = ButtonDefaults.outlinedButtonColors()
                                    .copy(
                                        containerColor = Color.White,
                                        contentColor = Color.Black,
                                        disabledContainerColor = Color.LightGray,
                                        disabledContentColor = Color.DarkGray
                                    )
                            ) {
                                Text(text = "OK")
                            }
                        }
                    }
                }
//                }
            }
        }
    }
}

@Composable
fun RecentScreenContent(
    bookmarks: LazyPagingItems<Bookmark>,
    navController: NavController,
    deleteAction: (Bookmark) -> Unit = {},
    recentScreenState: RecentScreenState = RecentScreenState(),
    folderSelectionState: FolderSelectionState = FolderSelectionState()
) {
    if (bookmarks.itemCount == 0) {
        EmptyBookmarkFolder()
    } else {
        Contents(bookmarks, navController, deleteAction, recentScreenState, folderSelectionState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Contents(
    bookmarks: LazyPagingItems<Bookmark>,
    navController: NavController,
    deleteAction: (Bookmark) -> Unit = {},
    recentScreenState: RecentScreenState,
    folderSelectionState: FolderSelectionState
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
            SearchBar()
            Spacer(modifier = Modifier.height(20.dp))
            Divider(
                color = Color.Black,
            )
        }

        stickyHeader() {
            AnimatedVisibility(
                recentScreenState.isSelectionMode
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    Divider(color = Color.Black)
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .background(Color.White)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(32.dp)) {
                            IconButton(
                                modifier = Modifier.fillMaxSize(),
                                onClick = {
                                    recentScreenState.deleteBookmarks(recentScreenState.selectedBookmarks) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Box(modifier = Modifier.size(32.dp)) {
                            IconButton(
                                modifier = Modifier.fillMaxSize(),
                                onClick = {
                                    folderSelectionState.toggleFolderSelection()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FolderOpen,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Divider(color = Color.Black)

//                    Box(modifier = Modifier.size(24.dp)) {
//                        IconButton(
//                            icon = FontAwesomeIcons.Solid.Tag
//                        ) {
                    //TODO: add tag selection method
//                }
//            }
                }
            }
        }

        items(
            count = bookmarks.itemCount,
            key = bookmarks.itemKey { it.id },
            contentType = { "bookmark" }
        ) { index ->

            val bookmark = bookmarks[index]!!
            val isSelected = recentScreenState.selectedBookmarks.contains(bookmark.id)

            SwipeBox(
                modifier = Modifier,
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
                    onClick = {

                            if (recentScreenState.isSelectionMode) {
                                recentScreenState.onSelectBookmark(bookmark.id, isSelected)
                            } else {
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
