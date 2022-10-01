package xyz.graphitenerd.tassel.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.model.Bookmark

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkCard(bookmark: Bookmark, modifier: Modifier = Modifier) {
    Log.d("checkvalue", "in bookmark card")
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(0.dp, 20.dp),
        elevation = 0.dp,
        shape = RectangleShape,
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(bookmark.rawUrl)
            }
            context.startActivity(intent)
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bookmark.imageUrl)
                        .size(60)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.placeholder),
                    contentDescription = "favicon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.clip(RectangleShape).size(72.dp).padding(4.dp)

                )
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bookmark.title?.let {
                            Text(
                                text = it,
                                maxLines = 2,
                                modifier = Modifier.weight(9f),
                                //                        modifier = Modifier.fillMaxWidth(0.8f),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(bookmark.favIcon)
                                .size(48)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.placeholder),
                            contentDescription = "favicon",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.clip(RectangleShape).size(24.dp)

                        )
//                            modifier = Modifier.padding(8.dp)
                    }

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = bookmark.rawUrl,
                        style = MaterialTheme.typography.subtitle2,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
//            Divider(
//                color = Color.Black,
//            )
        }
    }
}

@Composable
fun BookmarkCardOptions() {
    Card() {
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkCardPreview() {
    val sampleBookmark = Bookmark(
        title = "Compose layout basics  |  Jetpack Compose  |  Android Developers",
        rawUrl = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier",
        favIcon = "https://www.gstatic.com/devrel-devsite/prod/v84e6f6a61298bbae5bb110" +
            "c196e834c7f21fe3fb34e722925433ddb936d280c9/android/images/favicon.png"
    )

    BookmarkCard(sampleBookmark)
}
