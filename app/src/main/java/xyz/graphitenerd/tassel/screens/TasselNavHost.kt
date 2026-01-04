package xyz.graphitenerd.tassel.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import xyz.graphitenerd.tassel.model.SmartCollection
import xyz.graphitenerd.tassel.screens.collections.SmartCollectionScreen
import xyz.graphitenerd.tassel.screens.create.AddBookmarkScreen
import xyz.graphitenerd.tassel.screens.create.NewBookmarkViewModel
import xyz.graphitenerd.tassel.screens.folders.FolderScreen
import xyz.graphitenerd.tassel.screens.folders.FolderViewModel
import xyz.graphitenerd.tassel.screens.recents.BookmarkViewModel
import xyz.graphitenerd.tassel.screens.recents.RecentScreen
import xyz.graphitenerd.tassel.screens.settings.SettingsScreen

@Composable
fun TasselNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    addNewBookmarkViewModel: NewBookmarkViewModel = hiltViewModel(),
    bookmarkViewModel: BookmarkViewModel = hiltViewModel(),
    folderViewModel: FolderViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val recentScreenState = bookmarkViewModel.recentScreenStateFlow.collectAsStateWithLifecycle(
        LocalLifecycleOwner.current)
    val folderSelectionState = folderViewModel.folderSelectionStateFlow.collectAsStateWithLifecycle(
        LocalLifecycleOwner.current)

    val bookmarks = bookmarkViewModel.bookmarksPagingData.collectAsLazyPagingItems()
    val searchScreenState = bookmarkViewModel.searchScreenStateFlow.collectAsStateWithLifecycle(
        LocalLifecycleOwner.current)
    val smartCollections = bookmarkViewModel.smartCollectionsWithCounts.collectAsStateWithLifecycle(
        LocalLifecycleOwner.current)

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = Screens.RECENTS.name
    ) {
        composable(Screens.RECENTS.name) {
            RecentScreen(
                bookmarks = bookmarks,
                recentScreenState = recentScreenState.value,
                folderSelectionState = folderSelectionState.value,
                searchScreenState = searchScreenState.value,
                smartCollections = smartCollections.value,
                onCollectionClick = { collection ->
                    navController.navigate("${Screens.COLLECTION.name}/${collection.id}")
                },
                onBookmarkOpen = { bookmark ->
                    bookmarkViewModel.trackBookmarkOpen(bookmark)
                },
                navController = navController,
                onNavigateToAddNew = {
                    navController.navigate(Screens.ADDNEW.name)
                }
            )
        }

        composable(
            "${Screens.ADDNEW.name}?id={id}",
            arguments = listOf(
                navArgument("id") {
//                                    nullable = true
                    type = NavType.StringType
                    defaultValue = "0"
                }
            )
        ) { backStackEntry ->
            AddBookmarkScreen(
                addNewVM = addNewBookmarkViewModel,
                bookmarkViewModel = bookmarkViewModel,
                backStackEntry.arguments?.getString("id")?.toLong()
            )
        }
        composable(Screens.FOLDERS.name) {
            FolderScreen(
                VM = folderViewModel,
//                navController = navController
            )
        }
        composable(Screens.SETTINGS.name) {
            SettingsScreen(
                authViewModel = authViewModel,
            )
        }

        composable(
            "${Screens.COLLECTION.name}/{collectionId}",
            arguments = listOf(
                navArgument("collectionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")
            val collection = SmartCollection.getById(collectionId ?: "")

            collection?.let {
                val collectionBookmarks = bookmarkViewModel.getSmartCollectionBookmarks(it)
                    .collectAsLazyPagingItems()

                SmartCollectionScreen(
                    collection = it,
                    bookmarks = collectionBookmarks,
                    recentScreenState = recentScreenState.value,
                    onBackClick = { navController.popBackStack() },
                    deleteAction = { bookmark ->
                        bookmarkViewModel.deleteBookmark(listOf(bookmark.id))
                    },
                    onBookmarkOpen = { bookmark ->
                        bookmarkViewModel.trackBookmarkOpen(bookmark)
                    }
                )
            }
        }
    }
}

enum class Screens {
    RECENTS,
    FOLDERS,
    ADDNEW,
    SETTINGS,
    COLLECTION
}