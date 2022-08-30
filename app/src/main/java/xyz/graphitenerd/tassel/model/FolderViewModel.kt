package xyz.graphitenerd.tassel.model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun getFolderChildren(id: Long? = null) = bookmarkRepository.getFolders(id)

    fun refreshFolders(id: Long? = null) {
        _folders.value = getFolderChildren(id)
    }

    val _bookmarks: MutableStateFlow<List<Bookmark>> = MutableStateFlow(emptyList())

    val bookmarks: StateFlow<List<Bookmark>>
        get() = _bookmarks.asStateFlow()

    fun refreshBookmarks(id: Long) {
        _bookmarks.value = bookmarkRepository.getBookmarksByFolders(id)
    }

    fun refreshScreen(id: Long) {
        refreshFolders(id)
        refreshBookmarks(id)
        currentFolderId = id
    }

    fun insertFolder(folder: BookmarkFolder) {
        bookmarkRepository.insertFolder(folder)
    }
}
