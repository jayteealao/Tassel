package xyz.graphitenerd.tassel.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.chimbori.crux.articles.ArticleExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.reduce
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import java.net.Inet4Address
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) : ViewModel() {

    private val folder = MutableStateFlow("HOME")

    private val bookmarks: Deferred<Flow<PagingData<Bookmark>>>
        get() {
            return viewModelScope.async {
                bookmarkRepository.getAllBookmarks()
            }
        }

    private val bookmarkCount = bookmarkRepository.countBookmarks()

    private val viewUiState = MutableStateFlow(UiState.DEFAULT)

    private val bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)

    private val _state = MutableStateFlow(BookmarkUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<BookmarkUiState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                folder,
                bookmarks.await(),
                bookmarkCount,
                viewUiState,
                bottomNavBarState,
            ) { folder: String, bookmarks:PagingData<Bookmark>, bookmarkCount: Int, viewUiState: UiState, bottomNavBarState: ToggleButtonState->
                BookmarkUiState(
                    folderName = folder,
                    bookmarks = MutableStateFlow(bookmarks),
                    isEmpty = bookmarkCount == 0,
                    viewState = viewUiState,
                    bottomNavBarState = bottomNavBarState,
                )
            }.catch { throwable ->
                // TODO: emit a UI error here. For now we'll just rethrow
                throw throwable
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                viewUiState.value = UiState.LOADING
            }
            // TODO: look at result of runCatching and show any errors

            viewUiState.value = UiState.DEFAULT
        }
    }

    val addNewBookmarkForm = chassis<BookMarkForm> {
        BookMarkForm(
            title = field {
                reduce { copy(title = it) }
            },
            address = field {
                reduce { copy(address = it) }
            }
        )
    }

    private var _showAddNew = MutableStateFlow(false)

    val showAddNew: StateFlow<Boolean>
        get() = _showAddNew

    fun saveBookmarkForm() {
        with(addNewBookmarkForm()) {
            val address = address()
            val result = viewModelScope.launch(Dispatchers.IO) {
                val document = Jsoup.connect(address).get()
                val article = ArticleExtractor(address.toHttpUrl(), document)
                    .extractMetadata()
                    .article
                bookmarkRepository.addBookmark(
                    Bookmark(
                        id = 0,
                        title = article.title,
                        url = article.canonicalUrl.toUrl(),
                        favicon = article.faviconUrl?.toUrl(),
                        imageURl = article.imageUrl?.toUrl(),
                    )
                )
            }
        }
    }

    fun resetForm() {
        addNewBookmarkForm.reset()
    }

    fun toggleAddNew() {
        _showAddNew.update {
            !it
        }
    }
}

data class BookMarkForm(
    val title: Field<BookMarkForm, String?>,
    val address: Field<BookMarkForm, String>
)


