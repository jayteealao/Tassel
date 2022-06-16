package xyz.graphitenerd.tassel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import xyz.graphitenerd.tassel.ui.AddBookmark
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.theme.TasselTheme
import java.net.URL

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bookmarkViewModel: BookmarkViewModel by viewModels()
        setContent {
            TasselTheme {
                // A surface container using the 'background' color from the theme
                val uiState = bookmarkViewModel.state.collectAsState()
                RecentsScreen(
                    uiState = uiState.value,
                    onClickTasselButton = { bookmarkViewModel.toggleAddNew() }
                )
                val toggle = bookmarkViewModel.showAddNew.collectAsState()
                AddBookmark(
                    shouldDialogShow = toggle.value,
                    toggleShow = { bookmarkViewModel.toggleAddNew() },
                    onAccept = {
                        bookmarkViewModel.saveBookmarkForm()
                        bookmarkViewModel.toggleAddNew()
                               },
                    formState = bookmarkViewModel.addNewBookmarkForm)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
@Composable
fun MyApp() {
    val sampleBookmark = Bookmark(
        id = 1,
        title = "Compose layout basics  |  Jetpack Compose  |  Android Developers",
        url = URL("https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier"),
        favicon = URL(
            "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
                "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
        )
    )

    Log.d("checkvalue", "in App")
    Column(modifier = Modifier.fillMaxWidth()) {
        BookmarkCard(sampleBookmark)
        Text(text = "were here")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TasselTheme {
        Greeting("Android")
    }
}
