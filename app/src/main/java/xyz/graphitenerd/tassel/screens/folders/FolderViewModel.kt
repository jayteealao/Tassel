package xyz.graphitenerd.tassel.screens.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.repository.BookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder
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
        viewModelScope.launch(coroutineDispatcher) {
            _folders.value = getFoldersByParentId(id)
        }
    }

    private var _bookmarks: Flow<List<Bookmark>> = MutableStateFlow(emptyList())

    val bookmarks: Flow<List<Bookmark>>
        get() = _bookmarks

    private fun refreshBookmarks(id: Long) {
        viewModelScope.launch(coroutineDispatcher) {
            _bookmarks = bookmarkRepository.getBookmarksByFolders(id)
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
        bookmarkRepository.deleteBookmark(bookmark)
    }

    fun addBookmark(bookmark: Bookmark) = bookmarkRepository.addBookmark(bookmark)

    fun syncFoldersToCloud() = folderRepository.syncFoldersToCloud()

    fun saveAndSync(folder: BookmarkFolder) {
        viewModelScope.launch(coroutineDispatcher) {
            folderRepository.saveAndSyncFolder(folder)
        }
    }
}
