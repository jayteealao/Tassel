package xyz.graphitenerd.tassel.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.graphitenerd.tassel.ui.ToggleButtonState

enum class UiState {
    SEARCHING,
    LOADING,
    DEFAULT
}
data class BookmarkUiState(
    val folderName: String = "HOME",
    val bookmarks: List<Bookmark> = emptyList(),
    val viewState: UiState = UiState.DEFAULT,
    val bottomNavBarState: ToggleButtonState = ToggleButtonState.RECENTS,
)
