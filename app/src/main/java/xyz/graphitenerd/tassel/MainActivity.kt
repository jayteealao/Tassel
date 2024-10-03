package xyz.graphitenerd.tassel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.raqun.beaverlib.Beaver
import dagger.hilt.android.AndroidEntryPoint
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.screens.TasselNavHost
import xyz.graphitenerd.tassel.ui.BottomNavButton
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Beaver.isInitialized()) {
            Beaver.build(this)
        }

        setContent {

            // Remember a SystemUiController
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            val navController = rememberNavController()

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.White,
                    darkIcons = useDarkIcons
                )
                onDispose { }
            }
            TasselTheme {
                // A surface container using the 'background' color from the theme

                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    TasselNavHost(
                        navController = navController
                    )

                    AnimatedVisibility(
                        visible = navController
                            .currentBackStackEntryAsState()
                            .value?.destination?.route?.startsWith(Screens.ADDNEW.name)?.not() ?: false
                    ) {
                        Box(
                            modifier = Modifier
                                .background(color = Color(0xff, 0xff, 0xff, 128))
                                .fillMaxWidth()
                                .height(72.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            BottomNavButton(
                                navController
                            )
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
