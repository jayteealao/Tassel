package xyz.graphitenerd.tassel.data

import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val folderDao: FolderDao
) {

    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
//        return Pager(
//            PagingConfig(20)
//        ) {
//            data
//        }.flow
    }

    fun countBookmarks() = bookmarkDao.countBookmarks()

    fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)

    fun getRecentBookmarks() = bookmarkDao.getRecentBookmarks()

//    fun deleteBookmarkById(id: Long) = bookmarkDao.deleteBookmarkById(id)

    fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)

    fun getFolderById(id: Long) = folderDao.getFolderById(id)

    fun getFoldersByParentId(id: Long? = null) = folderDao.getFolderChildren(id)

    fun getFolderByName(name: String) = folderDao.getFolderByName(name)

    fun getBookmarksByFolders(id: Long) = bookmarkDao.getBookmarksByFolder(id)

    fun insertFolder(folder: BookmarkFolder) = folderDao.insertFolder(folder)
}
