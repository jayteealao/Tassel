package xyz.graphitenerd.tassel

import android.util.Log
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import xyz.graphitenerd.tassel.model.NewBookmarkViewModel
import xyz.graphitenerd.tassel.ui.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun RecentScreen(
    bookmarkViewModel: BookmarkViewModel,
    newBookmarkViewModel: NewBookmarkViewModel,
    onNavigateToAddNew: () -> Unit = {}
    authViewModel: AuthViewModel,
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
            bookmarksFlow = bookmarkViewModel.bookmarks,
            deleteAction = { bookmarkViewModel.deleteBookmark(it) }
        )
    }
}

@Composable
fun RecentScreenContent(bookmarksFlow: Flow<List<Bookmark>>, deleteAction: (Bookmark) -> Unit = {}) {
    val bookmarks: State<List<Bookmark>> = bookmarksFlow.collectAsState(emptyList())
    if (bookmarks.value.isEmpty()) {
        EmptyBookmarkFolder()
    } else {
        Contents(bookmarks.value, deleteAction)
    }
}

@Composable
fun RecentScreenContent(uiState: List<Bookmark>) {
    if (uiState.isEmpty()) {
        EmptyBookmarkFolder()
    } else {
        Contents(uiState)
    }
}

@Composable
private fun Contents(bookmarks: List<Bookmark>, deleteAction: (Bookmark) -> Unit = {}) {
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
        items(bookmarks, key = { it.id }) { bookmark ->
//            Log.e("tassel", "in recent screen column, current bookmark $bookmark")

            val delete = SwipeAction(
                icon = painterResource(id = R.drawable.icoutlinedelete),
                background = Color.Red,
                onSwipe = {
                    scope.launch(Dispatchers.IO) {
                        deleteAction(bookmark)
                    }
                }
            )
            SwipeableActionsBox(
                modifier = Modifier,
                endActions = listOf(delete),
                swipeThreshold = 96.dp,
                backgroundUntilSwipeThreshold = Color.White
            ) {
                BookmarkCard(bookmark = bookmark)
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
