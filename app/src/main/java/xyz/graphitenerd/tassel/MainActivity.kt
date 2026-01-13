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
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.screens.Screens
import xyz.graphitenerd.tassel.screens.TasselNavHost
import xyz.graphitenerd.tassel.ui.BottomNavButton
import xyz.graphitenerd.tassel.ui.NavDrawerContent
import xyz.graphitenerd.tassel.ui.theme.TasselTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Remember a SystemUiController
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.White,
                    darkIcons = useDarkIcons
                )
                onDispose { }
            }
            TasselTheme {
                ModalDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = drawerState.isOpen,
                    drawerContent = {
                        NavDrawerContent(
                            onSettingsClick = {
                                navController.navigate(Screens.SETTINGS.name)
                            },
                            onAboutClick = {
                                // TODO: Navigate to About screen
                            },
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        TasselNavHost(
                            navController = navController,
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            }
                        )

                        val currentRoute = navController
                            .currentBackStackEntryAsState()
                            .value?.destination?.route
                        
                        val showBottomNav = currentRoute != null && 
                            (currentRoute == Screens.RECENTS.name || 
                             currentRoute == Screens.FOLDERS.name)
                        
                        AnimatedVisibility(visible = showBottomNav) {
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
}
