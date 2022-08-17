package xyz.graphitenerd.tassel.model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(val bookmarkRepository: BookmarkRepository) : ViewModel() {

    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()

    private val bookmarkCount = bookmarkRepository.countBookmarks()

    private val bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)
}
