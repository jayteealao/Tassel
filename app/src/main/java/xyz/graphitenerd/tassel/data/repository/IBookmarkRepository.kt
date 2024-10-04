package xyz.graphitenerd.tassel.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.data.BookmarkId
import xyz.graphitenerd.tassel.model.Bookmark

interface IBookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun countBookmarks(): Flow<Int>
    fun addBookmark(bookmark: Bookmark): Long
    fun getRecentBookmarks(): Flow<List<Bookmark>>
    fun getBookmarkById(id: Long): Bookmark
    fun getBookmarksById(ids: List<Long>): List<Bookmark>
    fun getLastSavedBookmark(time: Long): List<Bookmark>
    fun getBookmarksByFolders(id: Long): Flow<List<Bookmark>>
    fun syncBookmarksToCloud()
    suspend fun saveBookmarkToCloud(bookmark: Bookmark) //TODO: move sync to service
    suspend fun saveAndSyncBookmark(bookmark: Bookmark)
    suspend fun syncUnsyncedBookmarks()
    fun bookmarksPagingData(): Flow<PagingData<Bookmark>>
    fun deleteBookmark(bookmarks: List<Bookmark>)
    fun deleteBookmark(bookmarkIds: Array<BookmarkId>)
    fun addBookmarks(bookmarks: List<Bookmark>)
}