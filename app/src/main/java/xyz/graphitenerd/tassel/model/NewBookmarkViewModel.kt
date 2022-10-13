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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.Repository
import xyz.graphitenerd.tassel.data.MetadataToBookmarkMapper
import xyz.graphitenerd.tassel.ui.FolderTree
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private var isEdit = false
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
            },
            folderTree = field {
                reduce {
                    copy(folderTree = it)
                }
            }
        )
    }

    fun previewBookmarkForm() {
        if ( isEdit ) {
            var bookmark = _bookmarkStateFlow.value as Bookmark
            with(bookmarkForm()) {
                bookmark = bookmark.copy(
                    title = title(),
                )
                Log.d("edit", "preview edit: $bookmark")
                _bookmarkStateFlow.value = bookmark

            }
            return
        }
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
                            bookmarkdata.folderId = folderTree().folderId
                            bookmarkForm.update(BookMarkForm::title, bookmarkdata.title)
                        }
                    }
                }
            }
        }
    }

    fun saveBookmarkForm() {
        if ( isEdit ) {
            viewModelScope.launch(Dispatchers.IO) {
                Log.d("edit", "saving edit: ${bookmarkStateFlow.value as Bookmark}")
                var bookmark = bookmarkStateFlow.value as Bookmark
                with(bookmarkForm()) {
                    bookmark = bookmark.copy(
                        title = title(),
                        folderId = folderTree().folderId
                        )
                }
                repository.saveAndSyncBookmark(bookmark)
            }
        } else {
            with(bookmarkForm()) {
                Log.e("tassel", "on save form address is ${address()}")
                viewModelScope.launch(Dispatchers.IO) {
                    if (Beaver.isInitialized()) {
                        val data = Beaver.load(address()).await()
                        Log.e("tassel", "metadata: $data")
                        if (data != null) {
                            repository.addBookmark(
                                metadataToBookmarkMapper.map(data).apply {
                                    folderId = folderTree().folderId
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadBookmark(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("edit", "$id")
            val _bookmark= repository.getBookmarkById(id)
            Log.d("edit", "$_bookmark")
            val _folder = repository.getFolderById(_bookmark.folderId!!)
            _bookmarkStateFlow.value = _bookmark
            bookmarkForm.update(BookMarkForm::title, _bookmark.title)
            bookmarkForm.update(BookMarkForm::address, _bookmark.rawUrl)
            bookmarkForm.update(
                BookMarkForm::folderTree,
                FolderTree(
                    folderName = _folder.name,
                    folderId = _folder.id,
                )
            )
        }
        isEdit = true
    }

    fun resetForm() {
        bookmarkForm.reset()
        _bookmarkStateFlow.value = EmptyBookmark
        isEdit = false
    }
}
