package xyz.graphitenerd.tassel.screens.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.model.BookMarkForm
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.EmptyBookmark
import xyz.graphitenerd.tassel.screens.recents.BookmarkViewModel
import xyz.graphitenerd.tassel.ui.FileTree
import xyz.graphitenerd.tassel.ui.FolderBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddBookmarkScreen(
    addNewVM: NewBookmarkViewModel,
    bookmarkViewModel: BookmarkViewModel,
    bookmarkId: Long? = 0,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val formChassis = addNewVM.bookmarkForm
    val formState by formChassis.state.collectAsState()
    val previewBookmark by addNewVM.bookmarkStateFlow.collectAsState()
    val uiState by addNewVM.uiState.collectAsState()
    val currentFolder by addNewVM.currentFolder.collectAsState()

    var showFolderSheet by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf<FileTree?>(null) }
    
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val isEditMode = bookmarkId != 0L

    DisposableEffect(bookmarkId) {
        if (bookmarkId != 0L) {
            addNewVM.loadBookmark(bookmarkId!!)
        }
        onDispose { addNewVM.resetForm() }
    }

    LaunchedEffect(showFolderSheet) {
        if (showFolderSheet) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    BackHandler(enabled = bottomSheetState.isVisible) {
        scope.launch {
            bottomSheetState.hide()
            showFolderSheet = false
        }
    }

    val url = formState.address.value.orEmpty()
    LaunchedEffect(url) {
        if (url.isBlank() || url.length < 8) return@LaunchedEffect
        delay(800)
        addNewVM.fetchMetadataForUrl(url)
    }

    FolderBottomSheet(
        sheetState = bottomSheetState,
        currentFolder = currentFolder,
        selectedFolder = selectedFolder,
        onNavigateToFolder = { addNewVM.refreshCurrentFolder(it) },
        onFolderSelected = { folder ->
            selectedFolder = if (folder.folderId == selectedFolder?.folderId) null else folder
        },
        onCreateFolder = { },
        onDismiss = {
            scope.launch {
                bottomSheetState.hide()
                showFolderSheet = false
            }
        },
        onConfirm = {
            selectedFolder?.let { formChassis.update(BookMarkForm::folderTree, it) }
            scope.launch {
                bottomSheetState.hide()
                showFolderSheet = false
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEditMode) "Edit Bookmark" else "New Bookmark",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    elevation = 0.dp
                )
            },
            bottomBar = {
                PrimaryActionBar(
                    isSaving = uiState.isSaving,
                    isEnabled = formState.address.value?.isNotBlank() == true,
                    saveError = uiState.saveError,
                    onClick = { addNewVM.saveBookmarkForm(onSuccess = onBack) }
                )
            },
            backgroundColor = Color.White
        ) { innerPadding ->
            AddBookmarkContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                urlValue = formState.address.value.orEmpty(),
                titleValue = formState.title.value.orEmpty(),
                selectedFolder = formState.folderTree.value,
                previewBookmark = if (previewBookmark == EmptyBookmark) null 
                    else previewBookmark as Bookmark,
                isFetchingMetadata = uiState.isFetchingMetadata,
                urlError = uiState.urlError,
                onUrlChange = { formChassis.update(BookMarkForm::address, it) },
                onTitleChange = { formChassis.update(BookMarkForm::title, it) },
                onFolderClick = { showFolderSheet = true }
            )
        }
    }
}

@Composable
private fun AddBookmarkContent(
    modifier: Modifier = Modifier,
    urlValue: String,
    titleValue: String,
    selectedFolder: FileTree?,
    previewBookmark: Bookmark?,
    isFetchingMetadata: Boolean,
    urlError: String?,
    onUrlChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onFolderClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Divider(color = Color.Black)
        Spacer(Modifier.height(16.dp))

        UrlFieldSection(
            value = urlValue,
            isLoading = isFetchingMetadata,
            error = urlError,
            onValueChange = onUrlChange
        )

        Spacer(Modifier.height(16.dp))

        TitleFieldSection(
            value = titleValue,
            onValueChange = onTitleChange
        )

        Spacer(Modifier.height(16.dp))

        FolderSelectorRow(
            selectedFolder = selectedFolder,
            onClick = onFolderClick
        )

        Spacer(Modifier.height(20.dp))
        Divider(color = Color.Black)
        Spacer(Modifier.height(12.dp))

        PreviewSection(
            isLoading = isFetchingMetadata,
            bookmark = previewBookmark
        )

        Spacer(Modifier.height(80.dp))
    }
}
