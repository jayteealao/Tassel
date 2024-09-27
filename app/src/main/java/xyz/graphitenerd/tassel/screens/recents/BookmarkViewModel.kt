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
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val storageService: StorageService,
    private val accountService: AccountService
    //TODO: inject coroutine dispatcher, best practice
) : ViewModel() {

    var bookmarkCount = MutableStateFlow(0)

    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getRecentBookmarks()

    var deletedBookmark: MutableStateFlow<Bookmark?> = MutableStateFlow(null)
        private set

    init {
        initializeBookmarkCount()
        initializeUserStorage()
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

    private fun initializeUserStorage() {
        if (accountService.hasUser() and !storageService.isUserSet()) {
            storageService.setUserId(accountService.getUserId())
        }
    }

}
