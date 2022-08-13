package xyz.graphitenerd.tassel.data

import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
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

}

