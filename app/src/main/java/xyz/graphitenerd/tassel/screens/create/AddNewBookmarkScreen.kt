package xyz.graphitenerd.tassel.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import xyz.graphitenerd.tassel.model.BookMarkForm
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.EmptyBookmark
import xyz.graphitenerd.tassel.screens.recents.BookmarkViewModel
import xyz.graphitenerd.tassel.ui.BookmarkCard
import xyz.graphitenerd.tassel.ui.FolderDropDownMenu

@Composable
fun AddBookmarkScreen(
    addNewVM: NewBookmarkViewModel,
    bookmarkViewModel: BookmarkViewModel,
    bookmarkId: Long? = 0
) {

    val formChassis = addNewVM.bookmarkForm
    val formState = formChassis.state.collectAsState()
    val previewBookmark = addNewVM.bookmarkStateFlow.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    val currentFolder by addNewVM.currentFolder.collectAsState()

    DisposableEffect(bookmarkId) {
        if (bookmarkId != 0L) {
            addNewVM.loadBookmark(bookmarkId!!)
        }
        onDispose { addNewVM.resetForm() }
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                bookmark = if (previewBookmark.value == EmptyBookmark) {
                    null
                } else { previewBookmark.value as Bookmark }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FolderDropDownMenu(
                fileTree = currentFolder,
                selectedFolder = formState.value.folderTree.value,
                isExpanded = isExpanded,
                onExpand = { isExpanded = !isExpanded },
                onNavigate = { addNewVM.refreshCurrentFolder(it) },
                onDismissRequest = { isExpanded = false },
                onFolderSelected = {
                    formChassis.update(BookMarkForm::folderTree, it)
                }
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
fun BookmarkPreview(
    show: Boolean,
    bookmark: Bookmark?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = show,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        if (bookmark != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Color.White)
            ) {
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
