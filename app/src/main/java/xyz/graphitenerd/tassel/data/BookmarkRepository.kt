package xyz.graphitenerd.tassel.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(private val bookmarkDao: BookmarkDao) {

    suspend fun getAllBookmarks(): Flow<PagingData<Bookmark>> {
        val data = bookmarkDao.getAllBookmarks()
        return Pager(
            PagingConfig(20)
        ) {
            data
        }.flow
    }

    fun countBookmarks() = bookmarkDao.countBookmarks()

    fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)

}

