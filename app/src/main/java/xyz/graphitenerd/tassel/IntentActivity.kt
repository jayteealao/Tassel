package xyz.graphitenerd.tassel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raqun.beaverlib.Beaver
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.model.*
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@AndroidEntryPoint
class IntentActivity : ComponentActivity() {
    @OptIn(
        ExperimentalAnimationApi::class, ExperimentalLifecycleComposeApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (!Beaver.isInitialized()) {
            Beaver.build(this)
        }
        Log.e("tassel", "beaver is initialized : ${Beaver.isInitialized()}")
        val VM: UIViewModel by viewModels()
        val addVM: NewBookmarkViewModel by viewModels()
        val data: Uri? = intent?.data ?: Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT))
        Log.d("received data", "$data")
        val formChassis = addVM.bookmarkForm
        setContent {
            var show by remember {
                mutableStateOf(true)
            }
            TasselTheme {
                val closeDialog by remember { mutableStateOf(true) }
                // A surface container using the 'background' color from the theme
                val uiState by VM.uiState.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)
                val formState = formChassis.state.collectAsState()
                val previewBookmark = addVM.bookmarkStateFlow.collectAsState()
                LaunchedEffect(key1 = data.toString()) {
                    Log.d("received data", "$data")
                    formChassis.update(BookMarkForm::address, data.toString())
                    formChassis.update(BookMarkForm::folderTree, FolderTree())
                    addVM.previewBookmarkForm()
                    addVM.saveBookmarkForm()
                }
                rememberCoroutineScope().launch {
                    delay(10000)
                    finish()
                }
                Surface(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent)
                        .clickable { show = false },
                    color = MaterialTheme.colors.background

                ) {
                    if (show) {

                        Dialog(
                            onDismissRequest = { /*TODO*/ }
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
                                ) {
                                    val bookmark = previewBookmark.value
                                    Log.e("bookmarkmarker", "$bookmark")
                                    Log.e("bookmarkmarker", "${bookmark.javaClass.simpleName}")
                                    if (bookmark is EmptyBookmark) {
                                        LoadingBookmark()
                                    } else if (bookmark is Bookmark) {
                                        BookmarkPreview(
                                            show = true,
                                            bookmark = bookmark,
                                            modifier = Modifier
                                        )
                                    }
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
