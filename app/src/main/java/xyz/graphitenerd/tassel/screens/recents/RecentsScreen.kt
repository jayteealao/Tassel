package xyz.graphitenerd.tassel.screens.recents

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.Screens
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.screens.AuthViewModel
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.EmptyBookmarkFolder
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.ui.SearchBar
import xyz.graphitenerd.tassel.utils.CustomSwipeableActionsBox
import xyz.graphitenerd.tassel.utils.SwipeAction

//import xyz.graphitenerd.tassel.utils.oneTimeWorkRequest
//import xyz.graphitenerd.tassel.utils.periodicWorkRequest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecentScreen(
    bookmarkViewModel: BookmarkViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    onNavigateToAddNew: () -> Unit = {},
) {
    val activityResultRegistry = LocalActivityResultRegistryOwner.current?.activityResultRegistry
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeAppBar(
                onClickMenuButton = {
                    val signInLauncher = activityResultRegistry?.register(
                        "sign-in",
                        FirebaseAuthUIActivityResultContract(),
                    ) { res ->
                        authViewModel.onSignInResult(res)
                    }
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        signInLauncher?.launch(authViewModel.signInIntent)
                    } else {
                        authViewModel.accountService.signOut()
                    }
//                                    bookmarkViewModel.loadJsonBookmarks()
                    // TODO: add else branch saying already logged in
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

        val snackbarState by bookmarkViewModel.deletedBookmark.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)
        val count by bookmarkViewModel.bookmarkCount.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)
        val bookmarksPagedData = bookmarkViewModel.bookmarksPagingData.collectAsLazyPagingItems()

        LaunchedEffect(true) {
            bookmarkViewModel.syncBookmarksToCloud()
        }

//        TODO: if a bookmark deletion is undone, trying to undelete it again doesnt show the snackbar
        LaunchedEffect(snackbarState) {
            var result: SnackbarResult? = null
            if (snackbarState != null) {
                result = scaffoldState.snackbarHostState.showSnackbar("Bookmark Deleted", "UNDO")
            }
            if (result == SnackbarResult.ActionPerformed) {
                withContext(Dispatchers.IO) {
                    bookmarkViewModel.addBookmark(snackbarState!!)
                }
            }
        }
        RecentScreenContent(
//            bookmarksFlow = bookmarkViewModel.bookmarks,
            bookmarks = bookmarksPagedData,
            navController = navController,
            deleteAction = { bookmarkViewModel.deleteBookmark(it) },
            count
        )
    }
}

@Composable
fun RecentScreenContent(
    bookmarks: LazyPagingItems<Bookmark>,
    navController: NavController,
    deleteAction: (Bookmark) -> Unit = {},
    bookmarkCount: Int = 0
) {
    if (bookmarks.itemCount == 0) {
        EmptyBookmarkFolder()
    } else {
        Contents(bookmarks, navController, deleteAction)
    }
}

@Composable
private fun Contents(
    bookmarks: LazyPagingItems<Bookmark>,
    navController: NavController,
    deleteAction: (Bookmark) -> Unit = {},
) {
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

        items(
            count = bookmarks.itemCount,
            key = bookmarks.itemKey { it.id },
            contentType = { "bookmark" }
        ) { index ->
            val delete = SwipeAction(
                icon = painterResource(id = R.drawable.icoutlinedelete),
                background = Color.Red,
                onSwipe = {
                    scope.launch(Dispatchers.IO) {
                        deleteAction(bookmarks[index]!!)
                    }
                }
            )

            val edit = SwipeAction(
                icon = rememberVectorPainter(Icons.Default.Edit),
                background = Color(0xFF7CB9E8),
                onSwipe = {
//                    Log.d("edit", "${Screens.ADDNEW.name}?id=${bookmark.id}")

                    navController.navigate("${Screens.ADDNEW.name}?id=${bookmarks[index]!!.id}")
                }
            )

            CustomSwipeableActionsBox(
                modifier = Modifier,
                startActions = listOf(edit),
                endActions = listOf(delete),
                swipeThreshold = 96.dp,
                backgroundUntilSwipeThreshold = Color.White
            ) {
                BookmarkCard(bookmark = bookmarks[index]!!)
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
