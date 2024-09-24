package xyz.graphitenerd.tassel.screens.recents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.repository.BookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.Tag
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import xyz.graphitenerd.tassel.ui.FolderTree
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    var bookmarkCount = MutableStateFlow(0)

    private val selectedTag = MutableStateFlow<Tag?>(null)
    val bookmarks: Flow<List<Bookmark>> = selectedTag.flatMapLatest { tag ->
        if (tag == null) {
            bookmarkRepository.getRecentBookmarks()
        } else {
            bookmarkRepository.getBookmarksByTag(tag.id)
        }
    }

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    val folderTree: FolderTree = FolderTree()

    init {
        initializeBookmarkCount()
        initializeFolderTree()
        initializeUserStorage()
    }

    fun updateSelectedTag(tag: Tag?) {
        selectedTag.value = tag
    }

    fun clearTagFilter() {
        selectedTag.value = null
    }

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepository.addBookmark(bookmark)
        }
    }

    fun getFolderChildren(id: Long? = null) = folderRepository.getFoldersByParentId(id)

//    var bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)

//    fun refreshTree() = folderTree.buildFolderTree(this)

    fun log(message: String) = Log.d("${this.javaClass.name}", message)

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        bookmarkRepository.deleteBookmark(bookmark)
    }

    fun syncBookmarksToCloud() {
        viewModelScope.launch(Dispatchers.IO) { bookmarkRepository.syncUnsyncedBookmarks() }
    }

    private fun initializeBookmarkCount() {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepository.countBookmarks().collect {
                bookmarkCount.value = it
            }
        }
    }

    private fun initializeFolderTree() {
        folderTree.buildFolderTree(this)
    }

    private fun initializeUserStorage() {
        if (accountService.hasUser() and !storageService.isUserSet()) {
            storageService.setUserId(accountService.getUserId())
        }
    }

}

