package xyz.graphitenerd.tassel.data

import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark

interface IBookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun countBookmarks(): Flow<Int>
    fun addBookmark(bookmark: Bookmark): Long
    fun getRecentBookmarks(): Flow<List<Bookmark>>
    fun getBookmarkById(id: Long): Bookmark
    fun getLastSavedBookmark(time: Long): List<Bookmark>
    fun deleteBookmark(bookmark: Bookmark)
    fun getBookmarksByFolders(id: Long): Flow<List<Bookmark>>
    fun syncBookmarksToCloud()
    suspend fun saveBookmarkToCloud(bookmark: Bookmark)
    suspend fun saveAndSyncBookmark(bookmark: Bookmark)
    suspend fun syncUnsyncedBookmarks()
}