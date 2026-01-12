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
import xyz.graphitenerd.tassel.model.DeletedFolderWithBookmarks
import xyz.graphitenerd.tassel.model.MovedFolderState
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

    // Multiselect state - declared before init to ensure proper initialization order
    private val _isSelectionMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _selectedFolders: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    private val _selectedBookmarks: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    private val _deletedFolders: MutableStateFlow<List<DeletedFolderWithBookmarks>> = MutableStateFlow(emptyList())
    private val _deletedBookmarksForUndo: MutableStateFlow<List<Bookmark>> = MutableStateFlow(emptyList())
    private val _movedFolders: MutableStateFlow<MovedFolderState?> = MutableStateFlow(null)

    // Public multiselect state exposure
    val selectedFolders: StateFlow<List<Long>> = _selectedFolders.asStateFlow()
    val selectedBookmarks: StateFlow<List<Long>> = _selectedBookmarks.asStateFlow()
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()
    val deletedFolders: StateFlow<List<DeletedFolderWithBookmarks>> = _deletedFolders.asStateFlow()
    val deletedBookmarks: StateFlow<List<Bookmark>> = _deletedBookmarksForUndo.asStateFlow()

    init {
        viewModelScope.launch(coroutineDispatcher) {
            currentFolder.value = getFolderById(currentFolderId)
        }
        refreshFolders(1L)
        refreshBookmarks(1)

        // Auto-exit selection mode when all items deselected
        viewModelScope.launch(coroutineDispatcher) {
            combine(_selectedFolders, _selectedBookmarks) { folders: List<Long>, bookmarks: List<Long> ->
                folders.isEmpty() && bookmarks.isEmpty()
            }.collect { allDeselected ->
                if (allDeselected && _isSelectionMode.value) {
                    _isSelectionMode.value = false
                }
            }
        }
    }

    private fun getFoldersByParentId(id: Long? = null) = folderRepository.getFoldersByParentId(id)

    private fun getFolderById(id: Long) = folderRepository.getFolderById(id)

    private fun refreshFolders(id: Long? = null) {
        _folders.value = emptyList()
        viewModelScope.launch(coroutineDispatcher) {
            _folders.value = getFoldersByParentId(id)
        }
    }

    private val _bookmarks: MutableStateFlow<List<Bookmark>> = MutableStateFlow(emptyList())
    val bookmarks: StateFlow<List<Bookmark>>
        get() = _bookmarks

    private fun refreshBookmarks(id: Long) {
        viewModelScope.launch(coroutineDispatcher) {
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

    // ========== Multiselect Functions ==========

    /**
     * Toggle the multiselect state of a folder.
     */
    fun toggleMultiSelectFolder(folderId: Long) {
        _selectedFolders.value = if (folderId in _selectedFolders.value) {
            _selectedFolders.value - folderId
        } else {
            _selectedFolders.value + folderId
        }
        updateSelectionModeFromSelection()
    }

    /**
     * Toggle the multiselect state of a bookmark.
     */
    fun toggleMultiSelectBookmark(bookmarkId: Long) {
        _selectedBookmarks.value = if (bookmarkId in _selectedBookmarks.value) {
            _selectedBookmarks.value - bookmarkId
        } else {
            _selectedBookmarks.value + bookmarkId
        }
        updateSelectionModeFromSelection()
    }

    /**
     * Toggle selection mode on/off.
     */
    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
    }

    /**
     * Enter selection mode.
     */
    fun enterSelectionMode() {
        _isSelectionMode.value = true
    }

    /**
     * Clear all selections and exit selection mode.
     */
    fun clearAllSelections() {
        _selectedFolders.value = emptyList()
        _selectedBookmarks.value = emptyList()
        _isSelectionMode.value = false
    }

    /**
     * Update selection mode based on current selections.
     * Auto-exit selection mode when all items are deselected.
     */
    private fun updateSelectionModeFromSelection() {
        // Auto-exit is handled by the combine flow in init block
    }

    /**
     * Select all folders and bookmarks in the current folder.
     */
    fun selectAllInCurrentFolder() {
        viewModelScope.launch(coroutineDispatcher) {
            val currentFolders = folderRepository.getFoldersByParentId(currentFolderId)
            val currentBookmarks = bookmarkRepository.getBookmarksByFolderId(currentFolderId)

            _selectedFolders.value = currentFolders.map { folder -> folder.id }
            _selectedBookmarks.value = currentBookmarks.map { bookmark -> bookmark.id }
            _isSelectionMode.value = true
        }
    }

    /**
     * Delete selected folders and bookmarks with undo support.
     */
    fun deleteSelectedItems() {
        viewModelScope.launch(coroutineDispatcher) {
            val deletedFoldersList = mutableListOf<DeletedFolderWithBookmarks>()
            val deletedBookmarksList = mutableListOf<Bookmark>()

            // Delete folders (with validation - skip HOME folder)
            _selectedFolders.value.forEach { folderId ->
                if (folderId == 1L) return@forEach // Skip HOME folder

                val folder = folderRepository.getFolderById(folderId)
                val bookmarks = bookmarkRepository.getBookmarksByFolderId(folderId)

                // Move bookmarks to parent folder (skip if parent is null)
                folder.parentId?.let { parentId ->
                    bookmarks.forEach { bookmark ->
                        launch(coroutineDispatcher) {
                            bookmarkRepository.updateBookmarkFolder(bookmark.id, parentId)
                        }
                    }
                }

                folderRepository.deleteFolderById(folderId)
                deletedFoldersList.add(DeletedFolderWithBookmarks(folder, bookmarks, folder.parentId))
            }

            // Delete bookmarks
            _selectedBookmarks.value.forEach { bookmarkId ->
                val bookmark = bookmarkRepository.getBookmarkById(bookmarkId)
                bookmarkRepository.deleteBookmark(listOf(bookmark))
                deletedBookmarksList.add(bookmark)
            }

            _deletedFolders.value = deletedFoldersList
            _deletedBookmarksForUndo.value = deletedBookmarksList

            clearAllSelections()
        }
    }

    /**
     * Undo the last delete operation.
     */
    fun undoDelete() {
        viewModelScope.launch(coroutineDispatcher) {
            // Restore folders
            _deletedFolders.value.forEach { deleted ->
                // Insert folder with id=0 to get auto-generated ID
                folderRepository.insertFolder(deleted.folder.copy(id = 0))
                // Get the newly inserted folder by name and parentId
                val restoredFolder = folderRepository.getFolderByName(deleted.folder.name)
                // Restore bookmarks to the newly created folder
                deleted.bookmarks.forEach { bookmark ->
                    bookmarkRepository.updateBookmarkFolder(bookmark.id, restoredFolder.id)
                }
            }

            // Restore bookmarks
            _deletedBookmarksForUndo.value.forEach { bookmark ->
                bookmarkRepository.addBookmark(bookmark)
            }

            _deletedFolders.value = emptyList()
            _deletedBookmarksForUndo.value = emptyList()
        }
    }

    /**
     * Move selected folders and bookmarks to the selected folder.
     * Folders are moved by updating their parentId.
     * Bookmarks are moved by updating their folderId.
     */
    fun moveSelectedItems() {
        viewModelScope.launch(coroutineDispatcher) {
            val targetFolderId = selectedFolder.value!!.folderId

            // Move folders by updating their parentId
            _selectedFolders.value.forEach { folderId ->
                if (folderId == 1L) return@forEach // Skip HOME folder

                val folder = folderRepository.getFolderById(folderId)
                folderRepository.insertFolder(folder.copy(parentId = targetFolderId))
            }

            // Move bookmarks by updating their folderId
            _selectedBookmarks.value.forEach { bookmarkId ->
                bookmarkRepository.updateBookmarkFolder(bookmarkId, targetFolderId)
            }

            clearAllSelections()
            refreshScreen(currentFolderId) // Refresh to show updated state
        }
    }

    // ========== End Multiselect Functions ==========

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
