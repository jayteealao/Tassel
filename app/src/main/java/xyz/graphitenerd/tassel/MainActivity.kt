package xyz.graphitenerd.tassel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raqun.beaverlib.Beaver
import dagger.hilt.android.AndroidEntryPoint
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import xyz.graphitenerd.tassel.model.NewBookmarkViewModel
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Beaver.build(this )
        Log.e("tassel", "beaver is initialized : ${Beaver.isInitialized()}")

        val bookmarkViewModel: BookmarkViewModel by viewModels()
        val newBookmarkViewModel: NewBookmarkViewModel by viewModels()
        setContent {
            TasselTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController= navController, startDestination = "recents") {
                    composable("recents") {
                        RecentScreen(
                            bookmarkViewModel,
                            newBookmarkViewModel,
                            onNavigateToAddNew = { navController.navigate("addNew") }
                        )
                    }
                    composable("addNew") { AddBookmarkScreen() }
                }
            }
        }
    }
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
