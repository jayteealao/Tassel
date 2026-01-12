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
import kotlinx.coroutines.withContext
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

    // Multiselect state
    private val _isSelectionMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _selectedFolders: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    private val _selectedBookmarks: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    private val _deletedFolders: MutableStateFlow<List<DeletedFolderWithBookmarks>> = MutableStateFlow(emptyList())
    private val _deletedBookmarksForUndo: MutableStateFlow<List<Bookmark>> = MutableStateFlow(emptyList())
    private val _movedFolders: MutableStateFlow<MovedFolderState?> = MutableStateFlow(null)

    // Data classes for multiselect state
    data class MovedFolderState(
        val folderIds: List<Long>,
        val oldParentIds: Map<Long, Long?>,
        val newParentId: Long?
    )

    data class DeletedFolderWithBookmarks(
        val folder: BookmarkFolder,
        val bookmarks: List<Bookmark>
    )

    data class FolderMultiSelectState(
        val selectedFolders: List<Long> = emptyList(),
        val selectedBookmarks: List<Long> = emptyList(),
        val isSelectionMode: Boolean = false,
        val deletedFolders: List<DeletedFolderWithBookmarks> = emptyList(),
        val deletedBookmarks: List<Bookmark> = emptyList(),
        val movedFolders: MovedFolderState? = null,
        val onSelectFolder: (Long, Boolean) -> Unit = { _, _ -> },
        val onSelectBookmark: (Long, Boolean) -> Unit = { _, _ -> },
        val toggleSelectionMode: () -> Unit = {},
        val clearSelectedItems: () -> Unit = {},
        val selectAll: () -> Unit = {},
        val deleteSelectedItems: () -> Unit = {},
        val moveSelectedBookmarks: () -> Unit = {},
        val moveSelectedFolders: () -> Unit = {},
        val undoFolderMove: () -> Unit = {},
        val undoFolderDelete: () -> Unit = {},
    )

    // Final combine using multiselect state flows (similar to BookmarkViewModel pattern)
    val folderMultiSelectStateFlow: StateFlow<FolderMultiSelectState> = combine(
        _selectedFolders,
        _selectedBookmarks,
        _isSelectionMode,
        _deletedFolders,
        _deletedBookmarksForUndo,
        _movedFolders
    ) { selectedFolders, selectedBookmarks, isSelectionMode, deletedFolders, deletedBookmarks, movedFolders ->
        FolderMultiSelectState(
            selectedFolders = selectedFolders,
            selectedBookmarks = selectedBookmarks,
            isSelectionMode = isSelectionMode,
            deletedFolders = deletedFolders,
            deletedBookmarks = deletedBookmarks,
            movedFolders = movedFolders,
            onSelectFolder = ::onSelectFolder,
            onSelectBookmark = ::onSelectBookmark,
            toggleSelectionMode = ::toggleSelectionMode,
            clearSelectedItems = ::clearSelectedItems,
            selectAll = ::selectAll,
            deleteSelectedItems = ::deleteSelectedItems,
            moveSelectedBookmarks = ::moveSelectedBookmarks,
            moveSelectedFolders = ::moveSelectedFolders,
            undoFolderMove = ::undoFolderMove,
            undoFolderDelete = ::undoFolderDelete,
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FolderMultiSelectState()
    )

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

    private fun createFolderInCurrent(name: String) {
        if (name.isBlank()) return
        val currentFolderId = _currentFolderDisplayed.value.folderId
        val currentFolderName = _currentFolderDisplayed.value.folderName
        val currentFolderParent = _currentFolderDisplayed.value.parent
        
        viewModelScope.launch(Dispatchers.IO) {
            val newFolder = BookmarkFolder(
                name = name,
                parentId = currentFolderId
            )
            // Save and wait for completion
            folderRepository.saveAndSyncFolder(newFolder)
            
            // Refresh on main thread after insert completes
            withContext(Dispatchers.Main) {
                val freshFileTree = FileTree(
                    folderName = currentFolderName,
                    folderId = currentFolderId,
                    parent = currentFolderParent
                )
                refreshCurrentFolder(freshFileTree)
            }
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
        updateBookmarkFolders = ::updateBookmarkFolders,
        createFolder = ::createFolderInCurrent
    ) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FolderSelectionState())
}

data class FolderSelectionState(
    val currentFolder: FileTree = FileTree(),
    val selectedFolder: FileTree? = null,
    val showFolderSelector: Boolean = false,
    val toggleFolderSelection: () -> Unit = {},
    val onClick: (FileTree) -> Unit = {},
    val onFolderSelected: (FileTree) -> Unit = {},
    val updateBookmarkFolders: (List<Long>) -> Unit = {},
    val createFolder: (String) -> Unit = {}
)
