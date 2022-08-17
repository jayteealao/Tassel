package xyz.graphitenerd.tassel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raqun.beaverlib.Beaver
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.longerThan
import io.github.boguszpawlowski.chassis.reduce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.data.MetadataToBookmarkMapper
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) : ViewModel() {

    private val metadataToBookmarkMapper = MetadataToBookmarkMapper()

    var _bookmarkStateFlow: MutableStateFlow<BookmarkMarker> = MutableStateFlow(EmptyBookmark)
    val bookmarkStateFlow = _bookmarkStateFlow.asStateFlow()

    val bookmarkForm = chassis<BookMarkForm> {
        BookMarkForm(
            title = field {
                reduce { copy(title = it) }
            },
            address = field {
                validators(longerThan(4))
                reduce {
                    copy(address = it)
                }
            }
        )
    }

    fun previewBookmarkForm() {
        with(bookmarkForm()) {
            viewModelScope.launch(Dispatchers.IO) {
                if (Beaver.isInitialized() and (address() != null)) {
                    Log.e("tassel", "on save form address is ${address()}")
                    val data = Beaver.load(address()).await()
//                    Log.e("tassel", "metadata: ${data.toString()}")
                    if (data != null) {
                        _bookmarkStateFlow.value = metadataToBookmarkMapper.map(data)
                        if ((bookmarkStateFlow.value != EmptyBookmark) and (title() == null)) {
                            val bookmarkdata = bookmarkStateFlow.value as Bookmark
                            bookmarkForm.update(BookMarkForm::title, bookmarkdata.title)
                        }
                    }
                }
            }
        }
    }

    fun saveBookmarkForm() {
        if (_bookmarkStateFlow.value != EmptyBookmark) {
            viewModelScope.launch(Dispatchers.IO) {
                bookmarkRepository.addBookmark(bookmarkStateFlow.value as Bookmark)
            }
        } else {
            with(bookmarkForm()) {
                Log.e("tassel", "on save form address is ${address()}")
                viewModelScope.launch(Dispatchers.IO) {
                    if (Beaver.isInitialized()) {
                        val data = Beaver.load(address()).await()
                        Log.e("tassel", "metadata: $data")
                        if (data != null) {
                            bookmarkRepository.addBookmark(metadataToBookmarkMapper.map(data))
                        }
                    }
                }
            }
        }
    }

    fun resetForm() {
        bookmarkForm.reset()
    }
}
