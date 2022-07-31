package xyz.graphitenerd.tassel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raqun.beaverlib.Beaver
import com.raqun.beaverlib.data.DataSource
import com.raqun.beaverlib.model.MetaData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkLocalDataSource
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import xyz.graphitenerd.tassel.ui.AddBookmark
import xyz.graphitenerd.tassel.ui.HomeAppBar
import xyz.graphitenerd.tassel.ui.theme.TasselTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Beaver.build(this, localDataSource = BookmarkLocalDataSource() )
        Log.e("tassel", "beaver is initialized : ${Beaver.isInitialized()}")

        val bookmarkViewModel: BookmarkViewModel by viewModels()
        setContent {
            TasselTheme {
                // A surface container using the 'background' color from the theme
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberBottomSheetScaffoldState()

                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    topBar = { HomeAppBar { scope.launch { scaffoldState.bottomSheetState.expand() } } },
                    sheetContent = {
                        Box(modifier = Modifier.wrapContentSize()) {
                            AddBookmark(
                                onAccept = {
                                    bookmarkViewModel.saveBookmarkForm()
                                    Log.e("tassel", "form submitted")
                                           },
                                formState = bookmarkViewModel.addNewBookmarkForm
                            )
                        }
                    },
                    sheetPeekHeight = 0.dp

                ) {
                    RecentsScreen(
                        bookmarksFlow = bookmarkViewModel.bookmarks
//                    uiState = list
                    )
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
