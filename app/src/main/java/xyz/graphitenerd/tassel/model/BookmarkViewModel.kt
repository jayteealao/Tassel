package xyz.graphitenerd.tassel.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raqun.beaverlib.Beaver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.data.MetadataToBookmarkMapper
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val bookmarkRepository: BookmarkRepository,
) : ViewModel() {

    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getRecentBookmarks()

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    val folderTree: FolderTree = FolderTree()

    init {
        folderTree.buildFolderTree(this)
    }

    fun addBookmark(bookmark: Bookmark) = bookmarkRepository.addBookmark(bookmark)

    fun getFolderChildren(id: Long? = null) = bookmarkRepository.getFoldersByParentId(id)

    private val bookmarkCount = bookmarkRepository.countBookmarks()

    var bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)

    fun refreshTree() = folderTree.buildFolderTree(this)

    fun log(message: String) = Log.d("${this.javaClass.name}", message)

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        bookmarkRepository.deleteBookmark(bookmark)
    }
}
