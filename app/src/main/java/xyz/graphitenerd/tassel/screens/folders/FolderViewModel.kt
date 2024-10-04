package xyz.graphitenerd.tassel.screens.folders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xyz.graphitenerd.tassel.data.repository.BookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.ui.FileTree
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _folders: MutableStateFlow<List<BookmarkFolder>> = MutableStateFlow(
        listOf(BookmarkFolder(id = 1, name = "HOME", parentId = null))
    )

    val folders: StateFlow<List<BookmarkFolder>>
        get() = _folders.asStateFlow()

    var currentFolderId: Long = 1
        private set

    var currentFolder: MutableStateFlow<BookmarkFolder?> = MutableStateFlow(null)
        private set

    init {
        viewModelScope.launch(coroutineDispatcher) {
            currentFolder.value = getFolderById(currentFolderId)
        }
        refreshFolders(1L)
        refreshBookmarks(1)
    }

    private fun getFoldersByParentId(id: Long? = null) = folderRepository.getFoldersByParentId(id)

    private fun getFolderById(id: Long) = folderRepository.getFolderById(id)

    private fun refreshFolders(id: Long? = null) {
        _folders.value = emptyList()
        viewModelScope.launch(coroutineDispatcher) {
            _folders.value = getFoldersByParentId(id)
        }
    }

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>>
        get() = _bookmarks

    private fun refreshBookmarks(id: Long) {
        viewModelScope.launch(coroutineDispatcher) {
        _bookmarks.value = emptyList()
            bookmarkRepository.getBookmarksByFolders(id).collect { bookmarks ->
                _bookmarks.value = bookmarks
            }
        }
    }

    fun refreshScreen(id: Long) {
        refreshFolders(id)
        refreshBookmarks(id)
        currentFolderId = id
        viewModelScope.launch(coroutineDispatcher) {
            currentFolder.value = getFolderById(id)
        }
    }

    fun insertFolder(folder: BookmarkFolder) {
        folderRepository.insertFolder(folder)
    }

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        bookmarkRepository.deleteBookmark(listOf(bookmark))
    }

    fun addBookmark(bookmark: Bookmark) = bookmarkRepository.addBookmark(bookmark)

    fun syncFoldersToCloud() = folderRepository.syncFoldersToCloud()

    fun saveAndSync(folder: BookmarkFolder) {
        viewModelScope.launch(coroutineDispatcher) {
            folderRepository.saveAndSyncFolder(folder)
        }
    }

    //folder selection state
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

    private var _currentFolderDisplayed: MutableStateFlow<FileTree> = MutableStateFlow(FileTree().run { withChildren { getFolderChildren(this) } })
    private val currentFolderDisplayed: StateFlow<FileTree> = _currentFolderDisplayed.asStateFlow()

    private val selectedFolder: MutableStateFlow<FileTree?> = MutableStateFlow(null)
    private val showFolderSelection: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private fun selectFolder(fileTree: FileTree) {
        Log.d("FolderViewModel, ", "selected folder: ${fileTree.folderName}")
        selectedFolder.value = fileTree
    }

    private fun toggleFolderSelection() {
        showFolderSelection.value = !showFolderSelection.value
        if (!showFolderSelection.value) {
            selectedFolder.value = null
            refreshCurrentFolder(FileTree())
        }
    }

    private fun refreshCurrentFolder(fileTree: FileTree) {
        _currentFolderDisplayed.value =
            fileTree.run { withChildren { getFolderChildren(this) } }
    }

    private fun updateBookmarkFolders(bookmarksId: List<Long>) {
        viewModelScope.launch(Dispatchers.IO){
            bookmarkRepository.getBookmarksById(bookmarksId).map { bookmark ->
                bookmark.copy(folderId = selectedFolder.value!!.folderId)
            }
                .let { bookmarkRepository.addBookmarks(it) }
            toggleFolderSelection()
        }
    }

     val folderSelectionStateFlow: StateFlow<FolderSelectionState> = combine(
        currentFolderDisplayed,
        selectedFolder,
        showFolderSelection
    ) { currentFolderDisplayed, selectedFolder, showFolderSelection -> FolderSelectionState(
        currentFolder = currentFolderDisplayed,
        selectedFolder = selectedFolder,
        showFolderSelector = showFolderSelection,
        toggleFolderSelection = { toggleFolderSelection() },
        onClick = ::refreshCurrentFolder,
        onFolderSelected = ::selectFolder,
        updateBookmarkFolders = ::updateBookmarkFolders
    ) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FolderSelectionState())
}

data class FolderSelectionState(
    val currentFolder: FileTree = FileTree(), //current file tree should be passed down
    val selectedFolder: FileTree? = null,
    val showFolderSelector: Boolean = false,
    val toggleFolderSelection: () -> Unit = {},
    val onClick: (FileTree) -> Unit = {}, // file tree should be updated here and children fetched
    val onFolderSelected: (FileTree) -> Unit = {},
    val updateBookmarkFolders: (List<Long>) -> Unit = {}
)
