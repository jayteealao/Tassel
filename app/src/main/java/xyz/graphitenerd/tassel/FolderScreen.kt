package xyz.graphitenerd.tassel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import xyz.graphitenerd.tassel.ui.FolderCard
import xyz.graphitenerd.tassel.ui.HomeAppBar

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun FolderScreen(VM: BookmarkViewModel) {

    val folders by VM.folders.collectAsStateWithLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { HomeAppBar(onClickTasselButton = {}) },
    ) {
        LazyColumn(modifier = Modifier) {
            items(folders, key = { it.id }) {
                Divider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = Color.Black
                )
                FolderCard(folder = it)

            }
        }
    }
}