package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import xyz.graphitenerd.tassel.R

@Composable
fun EmptyBookmarkFolder() {

    Surface(modifier = Modifier.fillMaxSize()) {
        
        Image(

            painter = painterResource(id = R.drawable.ic_emptyfolder),
            contentDescription = "",
            alignment = Alignment.BottomCenter
        )
    }
}

@Preview
@Composable
fun previewEF() {
    EmptyBookmarkFolder()
}