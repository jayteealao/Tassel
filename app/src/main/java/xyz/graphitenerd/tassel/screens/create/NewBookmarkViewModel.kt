package xyz.graphitenerd.tassel.screens.create

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
<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/create/NewBookmarkViewModel.kt
import xyz.graphitenerd.tassel.data.IBookmarkRepository
import xyz.graphitenerd.tassel.data.MetadataToBookmarkMapper
import xyz.graphitenerd.tassel.data.repository.IFolderRepository
import xyz.graphitenerd.tassel.model.BookMarkForm
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkMarker
import xyz.graphitenerd.tassel.model.EmptyBookmark
import xyz.graphitenerd.tassel.ui.FolderTree
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(
    private val bookmarkRepository: IBookmarkRepository,
    private val folderRepository: IFolderRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {
=======
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.data.MetadataToBookmarkMapper
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) : ViewModel() {
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/NewBookmarkViewModel.kt

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
            folder = field {
                reduce {
                    copy( folder = it )
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
                            bookmarkdata.folderId = folder().folderId
                            bookmarkForm.update(BookMarkForm::title, bookmarkdata.title)
                        }
                    }
                }
            }
        }
    }

    fun saveBookmarkForm() {
<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/create/NewBookmarkViewModel.kt
        if ( isEdit ) {
            viewModelScope.launch(coroutineDispatcher) {
//                Log.d("edit", "saving edit: ${bookmarkStateFlow.value as Bookmark}")
                var bookmark = bookmarkStateFlow.value as Bookmark
                with(bookmarkForm()) {
                    bookmark = bookmark.copy(
                        title = title(),
                        folderId = folderTree().folderId
                        )
                }
                bookmarkRepository.saveAndSyncBookmark(bookmark)
=======
        if (_bookmarkStateFlow.value != EmptyBookmark) {
            viewModelScope.launch(Dispatchers.IO) {
                bookmarkRepository.addBookmark(bookmarkStateFlow.value as Bookmark)
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/NewBookmarkViewModel.kt
            }
        } else {
            with(bookmarkForm()) {
                Log.e("tassel", "on save form address is ${address()}")
                viewModelScope.launch(Dispatchers.IO) {
                    if (Beaver.isInitialized()) {
                        val data = Beaver.load(address()).await()
                        Log.e("tassel", "metadata: $data")
                        if (data != null) {
<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/create/NewBookmarkViewModel.kt
                            bookmarkRepository.saveAndSyncBookmark(
=======
                            bookmarkRepository.addBookmark(
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/NewBookmarkViewModel.kt
                                metadataToBookmarkMapper.map(data).apply {
                                    folderId = folder().folderId
                                }
                            )
                        }
                    }
                }
            }
        }
    }

<<<<<<< ours:app/src/main/java/xyz/graphitenerd/tassel/screens/create/NewBookmarkViewModel.kt
    fun loadBookmark(id: Long) {
        viewModelScope.launch(coroutineDispatcher) {
            val _bookmark= bookmarkRepository.getBookmarkById(id)
            val _folder = folderRepository.getFolderById(_bookmark.folderId!!)
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

=======
>>>>>>> theirs:app/src/main/java/xyz/graphitenerd/tassel/model/NewBookmarkViewModel.kt
    fun resetForm() {
        bookmarkForm.reset()
    }
}
