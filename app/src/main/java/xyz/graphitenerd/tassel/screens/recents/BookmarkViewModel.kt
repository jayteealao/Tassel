package xyz.graphitenerd.tassel.screens.recents

import androidx.compose.ui.input.key.Key.Companion.F
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/recents/BookmarkViewModel.kt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import xyz.graphitenerd.tassel.ui.FolderTree
=======
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.ui.Folder
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/BookmarkViewModel.kt
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/recents/BookmarkViewModel.kt
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val coroutineDispatcher: CoroutineDispatcher
=======
    val bookmarkRepository: BookmarkRepository,
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/BookmarkViewModel.kt
) : ViewModel() {

    val _folders: MutableStateFlow<List<BookmarkFolder>> = MutableStateFlow(
        listOf(BookmarkFolder(id = 1, name = "HOME", parentId = null)))

<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/recents/BookmarkViewModel.kt
    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getRecentBookmarks()
=======
    val folders: StateFlow<List<BookmarkFolder>>
        get() = _folders.asStateFlow()
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/BookmarkViewModel.kt

    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()

    val folderTree: Folder = Folder()

    init {
        folderTree.buildFolderTree(this)
<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/recents/BookmarkViewModel.kt
        viewModelScope.launch(coroutineDispatcher) {
            bookmarkRepository.countBookmarks().collect {
                bookmarkCount.value = it
            }
        }
        if (accountService.hasUser() and !storageService.isUserSet()) {
            storageService.setUserId(accountService.getUserId())
        }
    }

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch(coroutineDispatcher) {
            bookmarkRepository.addBookmark(bookmark)
        }
    }

    fun getFolderChildren(id: Long? = null) = folderRepository.getFoldersByParentId(id)

    var bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)

    fun refreshTree() = folderTree.buildFolderTree(this)

//    fun log(message: String) = Log.d(this.javaClass.name, message)

    fun deleteBookmark(bookmark: Bookmark) {
        deletedBookmark.value = bookmark
        bookmarkRepository.deleteBookmark(bookmark)
    }

    fun syncBookmarksToCloud() {
        viewModelScope.launch(coroutineDispatcher) { bookmarkRepository.syncUnsyncedBookmarks() }
    }
=======
    }

    fun getFolderChildren(id: Long? = null) = bookmarkRepository.getFolders(id)

    fun refreshFolders(id: Long? = null) {
        _folders.value = getFolderChildren(id)
    }

    private val bookmarkCount = bookmarkRepository.countBookmarks()
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/BookmarkViewModel.kt

    var bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)
}
