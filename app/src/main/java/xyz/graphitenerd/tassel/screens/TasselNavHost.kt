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
                navController = navController
            )
        }
        composable(Screens.SETTINGS.name) {
            SettingsScreen(
                authViewModel = authViewModel,
            )
        }
    }
}

enum class Screens {
    RECENTS,
    FOLDERS,
    ADDNEW,
    SETTINGS
}