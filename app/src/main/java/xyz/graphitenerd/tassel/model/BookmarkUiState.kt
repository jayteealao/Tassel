package xyz.graphitenerd.tassel.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.jsoup.select.Evaluator
import xyz.graphitenerd.tassel.ui.ToggleButtonState

enum class UiState {
    SEARCHING,
    LOADING,
    DEFAULT
}
data class BookmarkUiState(
    val folderName: String = "HOME",
    val bookmarks: Flow<PagingData<Bookmark>> = MutableStateFlow(PagingData.empty()),
    val isEmpty: Boolean = true,
    val viewState: UiState = UiState.DEFAULT,
    val bottomNavBarState: ToggleButtonState = ToggleButtonState.RECENTS,
)
