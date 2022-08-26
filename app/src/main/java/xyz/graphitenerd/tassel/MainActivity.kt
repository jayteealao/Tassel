package xyz.graphitenerd.tassel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.raqun.beaverlib.Beaver
import dagger.hilt.android.AndroidEntryPoint
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import xyz.graphitenerd.tassel.model.NewBookmarkViewModel
import xyz.graphitenerd.tassel.ui.BottomNavButton
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Beaver.isInitialized()) {
            Beaver.build(this)
        }
        Log.e("tassel", "beaver is initialized : ${Beaver.isInitialized()}")

        val bookmarkViewModel: BookmarkViewModel by viewModels()
        setContent {
            TasselTheme {
                // A surface container using the 'background' color from the theme
                val bottomNavButtonState: ToggleButtonState by bookmarkViewModel.bottomNavBarState.collectAsState()

                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = Screens.RECENTS.name) {
                        composable(Screens.RECENTS.name) {
                            RecentScreen(
                                bookmarkViewModel = hiltViewModel(),
                                newBookmarkViewModel = hiltViewModel(),
                                onNavigateToAddNew = { navController.navigate(Screens.ADDNEW.name) }
                            )
                        }
                        composable(Screens.ADDNEW.name) {
                            AddBookmarkScreen(
                                addNewVM = hiltViewModel(),
                                bookmarkViewModel = hiltViewModel()
                            )
                        }
                        composable(Screens.FOLDERS.name) {
                            FolderScreen(VM = hiltViewModel())
                        }
                    }
                    AnimatedVisibility(
                        visible = navController
                            .currentBackStackEntryAsState()
                            .value?.destination?.route != Screens.ADDNEW.name
                    ) {
                        Box(
                            modifier = Modifier
                                .background(color = Color(0xff, 0xff, 0xff, 128))
                                .fillMaxWidth()
                                .height(72.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            BottomNavButton(
                                state = bottomNavButtonState,
                                onClick = { toggleButtonState ->
//                                    Log.d("debug tassel ", "backstack destination ${navController.currentBackStackEntry?.destination?.route}")
//                                    Log.d("debug tassel", "current destination ${navController.currentDestination?.route}")
                                    if (
                                        (navController.currentDestination?.route == Screens.RECENTS.name) and
                                        (toggleButtonState.name != ToggleButtonState.RECENTS.name)
                                    ) {
                                        navController.navigate(Screens.FOLDERS.name)
                                    } else {
                                        navController.navigate(Screens.RECENTS.name)
                                    }
                                } )
                        }
                    }
                }
            }
        }
    }
}
enum class Screens {
    RECENTS,
    FOLDERS,
    ADDNEW
}
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
val sampleBookmark = Bookmark(
    title = "Compose layout basics  |  Jetpack Compose  |  Android Developers",
    rawUrl = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier",
    favIcon = "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
        "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
)
val list = List(10) {
    sampleBookmark
}

@Composable
fun MyApp() {
//    val sampleBookmark = Bookmark(
//        id = 1,
//        title = "Compose layout basics  |  Jetpack Compose  |  Android Developers",
//        url = URL("https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier"),
//        favicon = URL(
//            "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
//                "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
//        )
//    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TasselTheme {
        Greeting("Android")
    }
}
