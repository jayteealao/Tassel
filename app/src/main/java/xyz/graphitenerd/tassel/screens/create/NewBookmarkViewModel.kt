package xyz.graphitenerd.tassel.screens.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.longerThan
import io.github.boguszpawlowski.chassis.reduce
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import xyz.graphitenerd.tassel.data.repository.BookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.BookMarkForm
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkMarker
import xyz.graphitenerd.tassel.model.EmptyBookmark
import xyz.graphitenerd.tassel.ui.FileTree
import xyz.graphitenerd.tassel.utils.OpenGraphunfurlExtension
import xyz.graphitenerd.tassel.utils.ogExtra
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var isEdit = false

    private var _bookmarkStateFlow: MutableStateFlow<BookmarkMarker> = MutableStateFlow(EmptyBookmark)
    val bookmarkStateFlow = _bookmarkStateFlow.asStateFlow()

    private val _uiState = MutableStateFlow(AddBookmarkUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val unfurler = Unfurler(
        extensions = listOf(OpenGraphunfurlExtension())
    )

    val bookmarkForm = chassis<BookMarkForm> {
        BookMarkForm(
            title = field {
                reduce { copy(title = it) }
            },
            address = field {
                validators(longerThan(4))
                reduce {
                    copy(address = it)
                }
            },
            folderTree = field {
                reduce {
                    copy(folderTree = it)
                }
            }
        )
    }

    fun previewBookmarkForm() {
        if ( isEdit ) {
            var bookmark = _bookmarkStateFlow.value as Bookmark
            with(bookmarkForm()) {
                bookmark = bookmark.copy(
                    title = title(),
                )
                _bookmarkStateFlow.value = bookmark

            }
            return
        }
        with(bookmarkForm()) {
            viewModelScope.launch(coroutineDispatcher) {
                if (address() != null) {
                    val unfurlResult = unfurler.unfurl(address())
                    val extra = unfurlResult?.ogExtra()
                    Log.d("tassel", "metadata: $extra")
                    if (unfurlResult != null) {
                        _bookmarkStateFlow.value = unfurlResult.toBookmarkMapper(address()).apply {
                            name = extra?.name
                            mediaType = extra?.mediaType
                        }
                        if ((bookmarkStateFlow.value != EmptyBookmark) and (title() == null)) {
                            val bookmarkData = bookmarkStateFlow.value as Bookmark
                            bookmarkData.folderId = folderTree().folderId
                            bookmarkForm.update(BookMarkForm::title, bookmarkData.title)
                        }
                    }
                }
            }
        }
    }

    fun fetchMetadataForUrl(url: String) {
        if (url.isBlank()) return
        
        viewModelScope.launch(coroutineDispatcher) {
            _uiState.value = _uiState.value.copy(isFetchingMetadata = true, urlError = null)
            try {
                val unfurlResult = unfurler.unfurl(url)
                val extra = unfurlResult?.ogExtra()
                
                val formState = bookmarkForm.state.value
                val currentFolderId = formState.folderTree.value?.folderId ?: 1L
                val currentTitle = formState.title.value
                
                if (unfurlResult != null) {
                    _bookmarkStateFlow.value = unfurlResult.toBookmarkMapper(url).apply {
                        name = extra?.name
                        mediaType = extra?.mediaType
                        folderId = currentFolderId
                    }
                    
                    if (currentTitle.isNullOrBlank() && unfurlResult.title != null) {
                        bookmarkForm.update(BookMarkForm::title, unfurlResult.title)
                    }
                    _uiState.value = _uiState.value.copy(isFetchingMetadata = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isFetchingMetadata = false,
                        urlError = "Couldn't fetch page info"
                    )
                    _bookmarkStateFlow.value = Bookmark(
                        rawUrl = url,
                        url = url,
                        title = currentTitle,
                        folderId = currentFolderId
                    )
                }
            } catch (e: Exception) {
                Log.e("tassel", "Error fetching metadata", e)
                _uiState.value = _uiState.value.copy(
                    isFetchingMetadata = false,
                    urlError = "Couldn't fetch page info"
                )
                val formState = bookmarkForm.state.value
                _bookmarkStateFlow.value = Bookmark(
                    rawUrl = url,
                    url = url,
                    title = formState.title.value,
                    folderId = formState.folderTree.value?.folderId ?: 1L
                )
            }
        }
    }

    fun saveBookmarkForm(onSuccess: () -> Unit = {}) {
        _uiState.value = _uiState.value.copy(isSaving = true, saveError = null)
        
        val formState = bookmarkForm.state.value
        val currentFolderId = formState.folderTree.value?.folderId ?: 1L
        val currentTitle = formState.title.value
        val currentAddress = formState.address.value
        
        if (isEdit) {
            viewModelScope.launch(coroutineDispatcher) {
                try {
                    var bookmark = bookmarkStateFlow.value as Bookmark
                    bookmark = bookmark.copy(
                        title = currentTitle,
                        folderId = currentFolderId
                    )
                    bookmarkRepository.saveAndSyncBookmark(bookmark)
                    _uiState.value = _uiState.value.copy(isSaving = false, bookmarkSaved = true)
                    withContext(Dispatchers.Main) { onSuccess() }
                } catch (e: Exception) {
                    Log.e("tassel", "Error saving bookmark", e)
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveError = "Couldn't save bookmark. Try again."
                    )
                }
            }
        } else {
            viewModelScope.launch(coroutineDispatcher) {
                try {
                    val currentPreview = _bookmarkStateFlow.value
                    if (currentPreview is Bookmark && currentPreview.rawUrl.isNotBlank()) {
                        val bookmarkToSave = currentPreview.copy(
                            title = currentTitle ?: currentPreview.title,
                            folderId = currentFolderId
                        )
                        Log.d("tassel", "Saving bookmark with folderId: ${bookmarkToSave.folderId}")
                        bookmarkRepository.saveAndSyncBookmark(bookmarkToSave)
                        _uiState.value = _uiState.value.copy(isSaving = false, bookmarkSaved = true)
                        withContext(Dispatchers.Main) { onSuccess() }
                    } else if (!currentAddress.isNullOrBlank()) {
                        val unfurlResult = unfurler.unfurl(currentAddress)
                        val extra = unfurlResult?.ogExtra()
                        if (unfurlResult != null) {
                            bookmarkRepository.saveAndSyncBookmark(
                                unfurlResult.toBookmarkMapper(currentAddress).apply {
                                    folderId = currentFolderId
                                    name = extra?.name
                                    mediaType = extra?.mediaType
                                }
                            )
                            _uiState.value = _uiState.value.copy(isSaving = false, bookmarkSaved = true)
                            withContext(Dispatchers.Main) { onSuccess() }
                        } else {
                            bookmarkRepository.saveAndSyncBookmark(
                                Bookmark(
                                    rawUrl = currentAddress,
                                    url = currentAddress,
                                    title = currentTitle,
                                    folderId = currentFolderId
                                )
                            )
                            _uiState.value = _uiState.value.copy(isSaving = false, bookmarkSaved = true)
                            withContext(Dispatchers.Main) { onSuccess() }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("tassel", "Error saving bookmark", e)
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveError = "Couldn't save bookmark. Try again."
                    )
                }
            }
        }
    }

    fun clearSavedState() {
        _uiState.value = _uiState.value.copy(bookmarkSaved = false)
    }

    fun loadBookmark(id: Long) {
        viewModelScope.launch(coroutineDispatcher) {
            val _bookmark= bookmarkRepository.getBookmarkById(id)
            val _folder = folderRepository.getFolderById(_bookmark.folderId!!)
            _bookmarkStateFlow.value = _bookmark
            bookmarkForm.update(BookMarkForm::title, _bookmark.title)
            bookmarkForm.update(BookMarkForm::address, _bookmark.rawUrl)
            bookmarkForm.update(
                BookMarkForm::folderTree,
                FileTree(
                    _folder.name,
                    _folder.id,
                    //TODO: add parent getter function here
                ).run { withChildren { getFolderChildren(this) } }
            )
        }
        isEdit = true
    }

    fun resetForm() {
        bookmarkForm.reset()
        _bookmarkStateFlow.value = EmptyBookmark
        isEdit = false
    }

    private fun getFolderChildren(fileTree: FileTree): List<FileTree> {
        return runBlocking {
            viewModelScope.async(coroutineDispatcher) {
                folderRepository.getFoldersByParentId(fileTree.folderId).map { folder ->
                    FileTree(
                        folder.name,
                        folder.id,
                        fileTree,
                    ).run { withChildren { getFolderChildren(this) } }
                }.toMutableList()
            }.await()
        }
    }
//TODO: add folder cache, map id to list of children, to avoid calling database on click

    private var _currentFolder: MutableStateFlow<FileTree> = MutableStateFlow(FileTree().run { withChildren { getFolderChildren(this) } })
    val currentFolder = _currentFolder.asStateFlow()

    fun refreshCurrentFolder(fileTree: FileTree) {
        _currentFolder.value =
            fileTree.run { withChildren { getFolderChildren(this) } }
    }
}

private fun UnfurlResult.toBookmarkMapper(rawUrl: String): Bookmark {

    return Bookmark(
        rawUrl = rawUrl,
        url = this.url.toString(),
        title = this.title,
        desc = this.description,
        favIcon = this.favicon.toString(),
        imageUrl = this.thumbnail.toString()
    )

}
