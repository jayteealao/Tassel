package xyz.graphitenerd.tassel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.raqun.beaverlib.Beaver
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.model.*
import xyz.graphitenerd.tassel.screens.create.BookmarkPreview
import xyz.graphitenerd.tassel.screens.create.NewBookmarkViewModel
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@ExperimentalMaterialApi
@AndroidEntryPoint
class IntentActivity : ComponentActivity() {

    /**
     * This activity is responsible for creating a new bookmark. It receives a URL from an intent and
     * displays a preview of the bookmark before saving it. The activity automatically closes after
     * a timeout period.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        if (!Beaver.isInitialized()) { Beaver.build(this) }

        val newBookmarkViewModel: NewBookmarkViewModel by viewModels()

        val receivedUrl: Uri? = intent?.data ?: Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT))

        val bookmarkForm = newBookmarkViewModel.bookmarkForm

        setContent {

            TasselTheme {
                val scope = rememberCoroutineScope()
                val previewBookmark = newBookmarkViewModel.bookmarkStateFlow.collectAsState()

                LaunchedEffect(key1 = receivedUrl.toString()) {
                    bookmarkForm.apply {
                        update(BookMarkForm::address, receivedUrl.toString())
                        update(BookMarkForm::folderTree, FolderTree())
                    }
                    newBookmarkViewModel.previewBookmarkForm()
                    newBookmarkViewModel.saveBookmarkForm()
                }

                LaunchedEffect(Unit) {
                    // Timeout for auto-closing the dialog
                    delay(10000)
                    finish()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    onClick = { scope.launch { finish() } },
                    color = Color.Transparent

                ) {

                    Dialog( onDismissRequest = {
                            scope.launch {
                                delay(300)
                                finish()
                            }
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    ) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            backgroundColor = Color.White
                        ) {
                            AnimatedContent(
                                targetState = previewBookmark.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) { bookmark ->
                                when (bookmark) {
                                    is EmptyBookmark -> LoadingBookmark()
                                    is Bookmark -> BookmarkPreview(show = true, bookmark = bookmark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingBookmark() {
    val shimmer = rememberShimmer(ShimmerBounds.View, tasselShimmerTheme)
    BookmarkPreview(
        show = true,
        bookmark = Bookmark(
            id = 1,
            rawUrl = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier",
            url = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier",
            favIcon = "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
                "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
        ),
        modifier = Modifier.shimmer(shimmer)
    )
}

val tasselShimmerTheme: ShimmerTheme = ShimmerTheme(
    animationSpec = infiniteRepeatable(
        animation = tween(
            800,
            easing = LinearEasing,
            delayMillis = 1_500,
        ),
        repeatMode = RepeatMode.Restart,
    ),
    blendMode = BlendMode.DstIn,
    rotation = 15.0f,
    shaderColors = listOf(
        Color.White.copy(alpha = 0.25f),
        Color.LightGray.copy(alpha = 0.00f),
        Color.White.copy(alpha = 0.25f),
        Color.LightGray.copy(alpha = 0.00f),
    ),
    shaderColorStops = listOf(
        0.0f,
        0.33f,
        0.66f,
        1.0f,
    ),
    shimmerWidth = 400.dp,
)
@Composable
fun Greeting2(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    TasselTheme {
        Greeting2("Android")
    }
}
