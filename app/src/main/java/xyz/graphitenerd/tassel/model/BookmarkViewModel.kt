package xyz.graphitenerd.tassel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.chimbori.crux.articles.ArticleExtractor
import com.raqun.beaverlib.Beaver
import com.raqun.beaverlib.model.MetaData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.reduce
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.internal.toImmutableList
import org.jsoup.Jsoup
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.data.MetadataToBookmarkMapper
import xyz.graphitenerd.tassel.ui.ToggleButtonState
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) : ViewModel() {

//    private val folder = MutableStateFlow("HOME")

    private val metadataToBookmarkMapper = MetadataToBookmarkMapper()

//    private val bookmarks: Deferred<Flow<PagingData<Bookmark>>>
//        get() {
//            return viewModelScope.async {
//                bookmarkRepository.getAllBookmarks()
//            }
//        }
    val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()

    private val bookmarkCount = bookmarkRepository.countBookmarks()

//    private val viewUiState = MutableStateFlow(UiState.DEFAULT)

    private val bottomNavBarState = MutableStateFlow(ToggleButtonState.RECENTS)

//    private val _state = MutableStateFlow(BookmarkUiState())

//    private val refreshing = MutableStateFlow(false)

//    val state: StateFlow<BookmarkUiState>
//        get() = _state

//    init {
//        viewModelScope.launch {
//            // Combines the latest value from each of the flows, allowing us to generate a
//            // view state instance which only contains the latest values.
//            combine(
//                folder,
//                bookmarks,
//                viewUiState,
//                bottomNavBarState,
//            ) { folder: String, bookmarks: List<Bookmark>, viewUiState: UiState, bottomNavBarState: ToggleButtonState->
//                BookmarkUiState(
//                    folderName = folder,
//                    bookmarks = bookmarks.toImmutableList(),
//                    viewState = viewUiState,
//                    bottomNavBarState = bottomNavBarState,
//                )
//            }.catch { throwable ->
//                // TODO: emit a UI error here. For now we'll just rethrow
//                throw throwable
//            }.collect {
//                _state.value = it
//            }
//        }
//
////        refresh(force = false)
//    }

//    private fun refresh(force: Boolean) {
//        viewModelScope.launch {
//            runCatching {
//                viewUiState.value = UiState.LOADING
//            }
//            // TODO: look at result of runCatching and show any errors
//
//            viewUiState.value = UiState.DEFAULT
//        }
//    }

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

//    @OptIn(ExperimentalCoroutinesApi::class)
    fun saveBookmarkForm() {
        with(addNewBookmarkForm()) {
            Log.e("tassel", "on save form address is ${address()}")
            viewModelScope.launch(Dispatchers.IO) {
                if (Beaver.isInitialized()) {
                    val data = Beaver.load(address()).await()
                    Log.e("tassel", "metadata: ${data.toString()}")
//                    if (data != null) {
//                        bookmarkRepository.addBookmark(metadataToBookmarkMapper.map(data))
//                    }
                }
            }
        }
    }

    fun resetForm() {
        addNewBookmarkForm.reset()
    }

}

data class BookMarkForm(
    val title: Field<BookMarkForm, String?>,
    val address: Field<BookMarkForm, String>
)


