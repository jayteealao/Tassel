package xyz.graphitenerd.tassel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import xyz.graphitenerd.tassel.screens.AuthViewModel
import xyz.graphitenerd.tassel.screens.Screens
import xyz.graphitenerd.tassel.screens.TasselNavHost
import xyz.graphitenerd.tassel.ui.BottomNavButton
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Remember a SystemUiController
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            val navController = rememberNavController()

            // Check authentication state to determine start destination
            val startDestination = if (authViewModel.hasUser()) {
                Screens.RECENTS.name
            } else {
                Screens.LOGIN.name
            }

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.White,
                    darkIcons = useDarkIcons
                )
                onDispose { }
            }
            TasselTheme {

                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    TasselNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )

                    AnimatedVisibility(
                        visible = navController
                            .currentBackStackEntryAsState()
                            .value?.destination?.route?.let { route ->
                                route != Screens.LOGIN.name &&
                                !route.startsWith(Screens.ADDNEW.name)
                            } ?: false
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
