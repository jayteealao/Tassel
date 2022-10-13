package xyz.graphitenerd.tassel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.Repository
import xyz.graphitenerd.tassel.model.service.AccountService
import xyz.graphitenerd.tassel.model.service.StorageService
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val repository: Repository,
    val storageService: StorageService,
    val accountService: AccountService
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
        viewModelScope.launch(Dispatchers.IO) {
            currentFolder.value = getFolderById(currentFolderId)
        }
        refreshFolders(1L)
        refreshBookmarks(1)
    }

    private fun getFoldersByParentId(id: Long? = null) = repository.getFoldersByParentId(id)

    private fun getFolderById(id: Long) = repository.getFolderById(id)

    private fun refreshFolders(id: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _folders.value = getFoldersByParentId(id)
        }
    }

    private var _bookmarks: Flow<List<Bookmark>> = MutableStateFlow(emptyList())

    val bookmarks: Flow<List<Bookmark>>
        get() = _bookmarks

    private fun refreshBookmarks(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarks = repository.getBookmarksByFolders(id)
        }
    }

    fun refreshScreen(id: Long) {
        refreshFolders(id)
        refreshBookmarks(id)
        currentFolderId = id
        viewModelScope.launch(Dispatchers.IO) {
            currentFolder.value = getFolderById(id)
        }
    }

    fun insertFolder(folder: BookmarkFolder) {
        repository.insertFolder(folder)
    }

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        repository.deleteBookmark(bookmark)
    }

    fun addBookmark(bookmark: Bookmark) = repository.addBookmark(bookmark)

    fun syncFoldersToCloud() = repository.syncFoldersToCloud()

    fun saveAndSync(folder: BookmarkFolder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAndSyncFolder(folder)
        }
    }
}
