package xyz.graphitenerd.tassel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.hilt.hiltViewModelScoped
import xyz.graphitenerd.tassel.model.*
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.SelectFolder

@Composable
fun AddBookmarkScreen(addNewVM: NewBookmarkViewModel, bookmarkViewModel: BookmarkViewModel) {

    val formChassis = addNewVM.bookmarkForm
    val formState = formChassis.state.collectAsState()
    val previewBookmark = addNewVM.bookmarkStateFlow.collectAsState()

    val tree = remember {
        bookmarkViewModel.folderTree
    }

    LaunchedEffect(true) {
        formChassis.update(BookMarkForm::folder, Folder())
        formChassis.update(BookMarkForm::folderTree, FolderTree())
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "New Bookmark",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            },
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to previous screen",
                        tint = Color.Black
                    )
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black,
            elevation = 0.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            FormInput(
                label = "Title",
                imageVector = Icons.Default.Info,
                value = formState.value.title.value ?: "",
                onValueChange = { formChassis.update(BookMarkForm::title, it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            FormInput(
                label = "Address",
                imageVector = Icons.Default.Build,
                value = formState.value.address.value ?: "",
                onValueChange = { formChassis.update(BookMarkForm::address, it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Black)
            BookmarkPreview(
                show = previewBookmark.value != EmptyBookmark,
                bookmark = if (previewBookmark.value == EmptyBookmark) { null
                   } else { previewBookmark.value as Bookmark }
            )
            SelectFolder(
                selectedFolder = formState.value.folder.value ?: Folder(),
                selectedFolder = formState.value.folderTree.value ?: FolderTree(),
                onSelect = {
                    formChassis.update(BookMarkForm::folder, it.content)
                    formChassis.update(BookMarkForm::folderTree, it.content)
                },
                tree = bookmarkViewModel.folderTree.buildBonsaiTree()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { addNewVM.previewBookmarkForm() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2D8445),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(40.dp, 0.dp)
                ) {
                    Text(text = "Preview")
                }
                Button(
                    onClick = { addNewVM.saveBookmarkForm() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(16.dp, 0.dp)
                ) {
                    Text(text = "Add Bookmark")
                }
            }
        }
    }
}

@Composable
fun BookmarkPreview(show: Boolean, bookmark: Bookmark?) {
    AnimatedVisibility(
        visible = show,
        modifier = Modifier.wrapContentHeight()
    ) {
        if (bookmark != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BookmarkCard(bookmark = bookmark)
                Divider(color = Color.Black)
            }
        }
    }
}

@Composable
fun FormInput(label: String, imageVector: ImageVector, value: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = imageVector, contentDescription = "$label icon")
            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedBorderColor = Color.Black)
        )
    }
}

@Preview
@Composable
fun ScreenPreview() {
    FormInput(label = "Title", imageVector = Icons.Default.Info, value = "google", onValueChange = {})
}
