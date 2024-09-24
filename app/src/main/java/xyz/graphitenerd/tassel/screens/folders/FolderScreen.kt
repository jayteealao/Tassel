package xyz.graphitenerd.tassel.screens.folders

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.navigation.NavController
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.FolderPlus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.Screens
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.FolderCard
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.utils.CustomSwipeableActionsBox
import xyz.graphitenerd.tassel.utils.SwipeAction

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FolderScreen(
    VM: FolderViewModel,
    navController: NavController
) {

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
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//                .padding(bottom = 72.dp)
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
            items(folders, key = { it.id }) {
                FolderCard(
                    folder = it,
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            VM.refreshScreen(it.id)
                        }
                    }
                )
            }
            items(bookmarks, key = { "bookmark${it.id}" }) {
//                BookmarkCard(bookmark = it)
//                Divider(
//                    modifier = Modifier.padding(horizontal = 20.dp),
//                    color = Color.Black
//                )

                val edit = SwipeAction(
                    icon = rememberVectorPainter(Icons.Default.Edit),
                    background = Color(0xFF7CB9E8),
                    onSwipe = {
                        Log.d("edit", "${Screens.ADDNEW.name}?id=${it.id}")

                        navController.navigate("${Screens.ADDNEW.name}?id=${it.id}")
                    }
                )

                val delete = SwipeAction(
                    icon = painterResource(id = R.drawable.icoutlinedelete),
                    background = Color.Red,
                    onSwipe = {
                        scope.launch(Dispatchers.IO) {
                            VM.deleteBookmark(it)
                        }
                    }
                )
                CustomSwipeableActionsBox(
                    modifier = Modifier,
                    startActions = listOf(edit),
                    endActions = listOf(delete),
                    swipeThreshold = 96.dp,
                    backgroundUntilSwipeThreshold = Color.White
                ) {
                    BookmarkCard(bookmark = it)
                    Divider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        color = Color.Black,
                    )
                }
            }
        }
    }
}
