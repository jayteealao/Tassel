package xyz.graphitenerd.tassel.screens.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.longerThan
import io.github.boguszpawlowski.chassis.reduce
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import xyz.graphitenerd.tassel.data.repository.BookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.BookMarkForm
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkMarker
import xyz.graphitenerd.tassel.model.EmptyBookmark
import xyz.graphitenerd.tassel.ui.FileTree
import xyz.graphitenerd.tassel.utils.OpenGraphunfurlExtension
import xyz.graphitenerd.tassel.utils.ogExtra
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var isEdit = false

    private var _bookmarkStateFlow: MutableStateFlow<BookmarkMarker> = MutableStateFlow(EmptyBookmark)
    val bookmarkStateFlow = _bookmarkStateFlow.asStateFlow()

    private val unfurler = Unfurler(
        extensions = listOf(OpenGraphunfurlExtension())
    )

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
//                Log.d("edit", "preview edit: $bookmark")
                _bookmarkStateFlow.value = bookmark

            }
            return
        }
        with(bookmarkForm()) {
            viewModelScope.launch(coroutineDispatcher) {
                if (address() != null) {
//                    Log.e("tassel", "on save form address is ${address()}")
                    val unfurlResult = unfurler.unfurl(address())
                    val extra = unfurlResult?.ogExtra()
                    Log.d("tassel", "metadata: $extra")
//                    val data = Beaver.load(address()).await()
//                    Log.e("tassel", "metadata: ${data.toString()}")
                    if (unfurlResult != null) {
                        _bookmarkStateFlow.value = unfurlResult.toBookmarkMapper(address()).apply {
                            name = extra?.name
                            mediaType = extra?.mediaType
                        }
                        if ((bookmarkStateFlow.value != EmptyBookmark) and (title() == null)) {
                            val bookmarkData = bookmarkStateFlow.value as Bookmark
                            bookmarkData.folderId = folderTree().folderId
                            bookmarkForm.update(BookMarkForm::title, bookmarkData.title)
                        }
                    }
                }
            }
        }
    }

    fun saveBookmarkForm() {
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
            }
        } else {
            with(bookmarkForm()) {
//                Log.e("tassel", "on save form address is ${address()}")
                viewModelScope.launch(coroutineDispatcher) {
//                    if (Beaver.isInitialized()) {
//                        val data = Beaver.load(address()).await()
                    val unfurlResult = unfurler.unfurl(address())
                    val extra = unfurlResult?.ogExtra()
                    Log.d("tassel", "metadata: $extra")
//                        Log.e("tassel", "metadata: $data")
                        if (unfurlResult != null) {
                            bookmarkRepository.saveAndSyncBookmark(
//                                metadataToBookmarkMapper.map(data).apply {
                                unfurlResult.toBookmarkMapper(address()).apply {
                                    folderId = folderTree().folderId
                                    name = extra?.name
                                    mediaType = extra?.mediaType
                                }
                            )
                        }
//                    }
                }
            }
        }
    }

    fun loadBookmark(id: Long) {
        viewModelScope.launch(coroutineDispatcher) {
            val _bookmark= bookmarkRepository.getBookmarkById(id)
            val _folder = folderRepository.getFolderById(_bookmark.folderId!!)
            _bookmarkStateFlow.value = _bookmark
            bookmarkForm.update(BookMarkForm::title, _bookmark.title)
            bookmarkForm.update(BookMarkForm::address, _bookmark.rawUrl)
            bookmarkForm.update(
                BookMarkForm::folderTree,
                FileTree(
                    _folder.name,
                    _folder.id,
                    //TODO: add parent getter function here
                ).run { withChildren { getFolderChildren(this) } }
            )
        }
        isEdit = true
    }

    fun resetForm() {
        bookmarkForm.reset()
        _bookmarkStateFlow.value = EmptyBookmark
        isEdit = false
    }

    private fun getFolderChildren(fileTree: FileTree): List<FileTree> {
        return runBlocking {
            viewModelScope.async(coroutineDispatcher) {
                folderRepository.getFoldersByParentId(fileTree.folderId).map { folder ->
                    FileTree(
                        folder.name,
                        folder.id,
                        fileTree,
                    ).run { withChildren { getFolderChildren(this) } }
                }.toMutableList()
            }.await()
        }
    }
//TODO: add folder cache, map id to list of children, to avoid calling database on click

    private var _currentFolder: MutableStateFlow<FileTree> = MutableStateFlow(FileTree().run { withChildren { getFolderChildren(this) } })
    val currentFolder = _currentFolder.asStateFlow()

    fun refreshCurrentFolder(fileTree: FileTree) {
        _currentFolder.value =
            fileTree.run { withChildren { getFolderChildren(this) } }
    }
}

private fun UnfurlResult.toBookmarkMapper(rawUrl: String): Bookmark {

    return Bookmark(
        rawUrl = rawUrl,
        url = this.url.toString(),
        title = this.title,
        desc = this.description,
        favIcon = this.favicon.toString(),
        imageUrl = this.thumbnail.toString()
    )

}
