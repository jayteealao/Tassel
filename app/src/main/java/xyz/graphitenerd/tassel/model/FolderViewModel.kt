package xyz.graphitenerd.tassel.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkRepository
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    val _folders: MutableStateFlow<List<BookmarkFolder>> = MutableStateFlow(
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

    fun getFoldersByParentId(id: Long? = null) = bookmarkRepository.getFoldersByParentId(id)

    fun getFolderById(id: Long) = bookmarkRepository.getFolderById(id)

    fun refreshFolders(id: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _folders.value = getFoldersByParentId(id)
        }
    }

    var _bookmarks: Flow<List<Bookmark>> = MutableStateFlow(emptyList())

    val bookmarks: Flow<List<Bookmark>>
        get() = _bookmarks

    fun refreshBookmarks(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarks = bookmarkRepository.getBookmarksByFolders(id)
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
        bookmarkRepository.insertFolder(folder)
    }

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        bookmarkRepository.deleteBookmark(bookmark)
    }

    fun addBookmark(bookmark: Bookmark) = bookmarkRepository.addBookmark(bookmark)
}
