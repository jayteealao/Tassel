package xyz.graphitenerd.tassel.screens.recents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkId
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

    val bookmarksPagingData: StateFlow<PagingData<Bookmark>> = bookmarkRepository.bookmarksPagingData()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())

    var deletedBookmarks: MutableStateFlow<List<Bookmark>> = MutableStateFlow(emptyList())
        private set

    private var _isSelectionMode = MutableStateFlow(false)

    private var _selectedBookmarks = MutableStateFlow<List<Long>>(emptyList())

    /**
     * StateFlow representing the current state of the Recent screen.
     *
     * This flow combines the following states:
     * - `_selectedBookmarks`: The currently selected bookmarks.
     * - `_isSelectionMode`: Whether the selection mode is active.
     * - `deletedBookmarks`: The bookmarks that have been deleted.
     *
     * The resulting `RecentScreenState` includes the combined state and exposes functions for:
     * - Selecting bookmarks.
     * - Toggling the selection mode.
     * - Clearing the selected bookmarks.
     * - Deleting bookmarks.
     * - Adding bookmarks.
     *
     * The state is shared using `stateIn` within the `viewModelScope`, starting emission immediately
     * and keeping the last emitted value for 5 seconds after the last subscriber disappears.
     */
    val recentScreenStateFlow: StateFlow<RecentScreenState> = combine(
        _selectedBookmarks,
        _isSelectionMode,
        deletedBookmarks,
    ) { selectedBookmarks, isSelectionMode, deletedBookmarks -> RecentScreenState(
        selectedBookmarks = selectedBookmarks,
        isSelectionMode = isSelectionMode,
        deletedBookmarks = deletedBookmarks,
        onSelectBookmark = ::onSelectBookmark,
        toggleSelectionMode = ::toggleSelectionMode,
        clearSelectedBookmarks = ::clearSelectedBookmarks,
        deleteBookmarks = ::deleteBookmark,
        addBookmarks = ::addBookmarks
    ) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), RecentScreenState())

    private fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
    }

    private fun addSelectedBookmark(bookmarkId: Long) {
        _selectedBookmarks.value += bookmarkId
    }

    private fun removeSelectedBookmark(bookmarkId: Long) {
        _selectedBookmarks.value -= bookmarkId
    }

    private fun onSelectBookmark(bookmarkId: Long, isSelected: Boolean) {
        if (isSelected) {
            removeSelectedBookmark(bookmarkId)
        } else {
            addSelectedBookmark(bookmarkId)
        }
    }

    private fun clearSelectedBookmarks() {
        _selectedBookmarks.value = emptyList()
        _isSelectionMode.value = false
    }

    /**
     * Initialization block for the ViewModel.
     *
     * - Initializes user storage by setting the user ID in the storage service if a user is logged in.
     * - Collects the `recentScreenStateFlow` and automatically disables selection mode if all bookmarks
     *   are deselected while the selection mode is active.
     */
    init {
        initializeUserStorage()
        viewModelScope.launch {
            var previouslyEmpty = true
            recentScreenStateFlow.collectIndexed { index, value ->
                if (!previouslyEmpty && value.selectedBookmarks.isEmpty() && value.isSelectionMode) {
                    _isSelectionMode.value = false
                    previouslyEmpty = true
                } else {
                    previouslyEmpty = value.selectedBookmarks.isEmpty()
                }
            }
        }
    }

    fun addBookmarks(bookmarks: List<Bookmark>) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepository.addBookmarks(bookmarks)
        }
    }

    fun getFolderChildren(id: Long? = null) = folderRepository.getFoldersByParentId(id)

    fun log(message: String) = Log.d(this.javaClass.name, message)

    fun deleteBookmark(bookmarkIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            deletedBookmarks.value += bookmarkRepository.getBookmarksById(bookmarkIds)
            bookmarkRepository.deleteBookmark(bookmarkIds.map { BookmarkId(it) }.toTypedArray())
        }
    }

    fun syncBookmarksToCloud() {
        viewModelScope.launch(Dispatchers.IO) { bookmarkRepository.syncUnsyncedBookmarks() }
    }

    private fun initializeUserStorage() {
        if (accountService.hasUser() and !storageService.isUserSet()) {
            storageService.setUserId(accountService.getUserId())
        }
    }

}

data class RecentScreenState(
    val selectedBookmarks: List<Long> = emptyList(),
    val isSelectionMode: Boolean = false,
    val deletedBookmarks: List<Bookmark> = emptyList(),
    val onSelectBookmark: (Long, Boolean) -> Unit = { _, _ -> }, //if selected remove else add
    val toggleSelectionMode: () -> Unit = {},
    val clearSelectedBookmarks: () -> Unit = {},
    val deleteBookmarks: (List<Long>) -> Unit = {},
    val addBookmarks: (List<Bookmark>) -> Unit = {},
)