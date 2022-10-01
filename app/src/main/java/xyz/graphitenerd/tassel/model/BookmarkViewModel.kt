package xyz.graphitenerd.tassel.model

import androidx.compose.ui.input.key.Key.Companion.F
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    val bookmarkRepository: BookmarkRepository,
) : ViewModel() {

    val _folders: MutableStateFlow<List<BookmarkFolder>> = MutableStateFlow(
        listOf(BookmarkFolder(id = 1, name = "HOME", parentId = null)))

    val folders: StateFlow<List<BookmarkFolder>>
        get() = _folders.asStateFlow()

    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()

    val folderTree: Folder = Folder()
    val folderTree: FolderTree = FolderTree()

    init {
        folderTree.buildFolderTree(this)
    }

    fun getFolderChildren(id: Long? = null) = bookmarkRepository.getFolders(id)

    fun refreshFolders(id: Long? = null) {
        _folders.value = getFolderChildren(id)
    }

    private val bookmarkCount = bookmarkRepository.countBookmarks()

    var bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)
}
