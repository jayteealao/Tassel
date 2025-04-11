package xyz.graphitenerd.tassel.screens.folders

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.FolderPlus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.FolderCard
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.ui.SwipeBox

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FolderScreen(
    VM: FolderViewModel,
) {
    val lazyListState = rememberLazyListState()
    val folders by VM.folders.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)
    val bookmarks by VM.bookmarks.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle, initialValue = emptyList())
    val currentFolder by VM.currentFolder.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)
    var newFolderName by remember {
        mutableStateOf("")
    }
    var showNewFolderInput by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val snackbarState by VM.deletedBookmark.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)

    BackHandler(currentFolder?.parentId != null && currentFolder?.id != 1L) {
        currentFolder?.parentId?.let { VM.refreshScreen(it) }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Column() {
                HomeAppBar(
                    actionIcon = rememberVectorPainter(image = FontAwesomeIcons.Solid.FolderPlus),
                    onClickActionButton = {
                        showNewFolderInput = !showNewFolderInput
                    }
                )
                Divider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = Color.Black
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
        LaunchedEffect(snackbarState) {
            var result: SnackbarResult? = null
            if (snackbarState != null) {
                result = scaffoldState.snackbarHostState.showSnackbar("Bookmark Deleted", "UNDO")
            }
            if (result == SnackbarResult.ActionPerformed) {
                withContext(Dispatchers.IO) {
                    VM.addBookmark(snackbarState!!)
                }
            }
        }

        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(20.dp, 0.dp, 20.dp, 78.dp)
        ) {
            item {
                AnimatedVisibility(visible = showNewFolderInput) {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newFolderName,
                                onValueChange = { newFolderName = it },
                                placeholder = { Text(text = "Folder Name") },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(end = 8.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (newFolderName.isNotBlank()) {
                                        scope.launch(Dispatchers.IO) {
                                            VM.saveAndSync(
                                                BookmarkFolder(
                                                    name = newFolderName,
                                                    parentId = VM.currentFolderId
                                                )
                                            )
                                            VM.refreshScreen(id = VM.currentFolderId)
                                            newFolderName = ""
                                            showNewFolderInput = false
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.folder_plus_outline),
                                    contentDescription = "Add New Folder Button",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Divider(color = Color.Black)
                    }
                }
            }

            items(
                count = folders.size,
                key = { folders[it].id },
                contentType = { "folder" }
            ) { index ->
                val folder = folders[index]
                FolderCard(
                    folder = folder,
                    onClick = {
                        scope.launch(Dispatchers.Main) {
                            VM.refreshScreen(folder.id)
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                )
            }

            items(
                count = bookmarks.size,
                key = { bookmarks[it].id },
                contentType = { "bookmark" }
            ) { index ->
                val bookmark = bookmarks[index]
                SwipeBox(
                    modifier = Modifier,
                    onDelete = {
                        scope.launch(Dispatchers.IO) {
                            VM.deleteBookmark(bookmark)
                        }
                    },
                    onEdit = {}
                ) {
                    BookmarkCard(bookmark = bookmark)
                    Divider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        color = Color.Black,
                    )
                }

            }

//            items(bookmarks, key = { "bookmark${it.id}" }) {
//                SwipeBox(
//                    modifier = Modifier,
//                    onDelete = {
//                        scope.launch(Dispatchers.IO) {
//                            VM.deleteBookmark(it)
//                        }
//                    },
//                    onEdit = {}
//                ) {
//                    BookmarkCard(bookmark = it)
//                    Divider(
//                        modifier = Modifier.align(Alignment.BottomCenter),
//                        color = Color.Black,
//                    )
//                }
//            }
        }
    }
}
