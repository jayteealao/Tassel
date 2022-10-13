package xyz.graphitenerd.tassel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.Repository
import xyz.graphitenerd.tassel.model.service.AccountService
import xyz.graphitenerd.tassel.model.service.StorageService
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: Repository,
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    var bookmarkCount = MutableStateFlow(0)

    val bookmarks: Flow<List<Bookmark>> = repository.getRecentBookmarks()

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    val folderTree: FolderTree = FolderTree()

    init {
        folderTree.buildFolderTree(this)
        viewModelScope.launch(Dispatchers.IO) {
            repository.countBookmarks().collect {
                bookmarkCount.value = it
            }
        }
        if (accountService.hasUser() and !storageService.isUserSet()) {
            storageService.setUserId(accountService.getUserId())
        }
    }

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBookmark(bookmark)
        }
    }

    fun getFolderChildren(id: Long? = null) = repository.getFoldersByParentId(id)

    var bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)

    fun refreshTree() = folderTree.buildFolderTree(this)

    fun log(message: String) = Log.d("${this.javaClass.name}", message)

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        repository.deleteBookmark(bookmark)
    }

    fun syncBookmarksToCloud() {
        viewModelScope.launch(Dispatchers.IO) { repository.syncUnsyncedBookmarks() }
    }

}
