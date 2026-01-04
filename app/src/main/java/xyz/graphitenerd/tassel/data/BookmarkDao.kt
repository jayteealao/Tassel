package xyz.graphitenerd.tassel.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark

@Dao
interface BookmarkDao {

    @Upsert
    fun addBookmark(bookmark: Bookmark): Long

    @Upsert
    fun addBookmarks(bookmarks: List<Bookmark>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: Bookmark): Long

    @Delete
    fun deleteBookmark(bookmarks: List<Bookmark>)

    @Delete(entity = Bookmark::class)
    fun deleteBookmark(vararg bookmarkIds: BookmarkId)

    @Query("DELETE FROM bookmark WHERE rawUrl = :url")
    fun deleteBookmarkWithUrl(url: String)

    @Query("SELECT * FROM bookmark WHERE synced = 0")
    fun getUnsyncedBookmarks(): List<Bookmark>

    @Query("SELECT * FROM bookmark")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmark ORDER BY creation_date DESC LIMIT 20")
    fun getRecentBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmark WHERE creation_date > :time ORDER BY creation_date DESC")
    fun getLastSavedBookmark(time: Long): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getBookmarkById(id: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE id IN (:ids)")
    fun getBookmarksById(ids: List<Long>): List<Bookmark>

    @Query("""
        SELECT * FROM bookmark
        WHERE url LIKE '%' || :search || '%'
        OR title LIKE '%' || :search || '%'
        OR `desc` LIKE '%' || :search || '%'
        OR rawUrl LIKE '%' || :search || '%'
    """)
    fun searchBookmarks(search: String): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE folderId LIKE :folderId")
    fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>>

    @Query("SELECT count(*) FROM bookmark")
    fun countBookmarks(): Flow<Int>

    @Query("SELECT * FROM bookmark ORDER BY creation_date DESC")
    fun bookmarksPagingSource(): PagingSource<Int, Bookmark>

    // Smart Collections Queries

    @Query("SELECT * FROM bookmark WHERE isRead = 0 ORDER BY creation_date DESC")
    fun getUnreadBookmarks(): PagingSource<Int, Bookmark>

    @Query("SELECT COUNT(*) FROM bookmark WHERE isRead = 0")
    fun countUnreadBookmarks(): Flow<Int>

    @Query("SELECT * FROM bookmark WHERE creation_date > :timestamp ORDER BY creation_date DESC")
    fun getRecentlyAddedBookmarks(timestamp: Long): PagingSource<Int, Bookmark>

    @Query("SELECT COUNT(*) FROM bookmark WHERE creation_date > :timestamp")
    fun countRecentlyAddedBookmarks(timestamp: Long): Flow<Int>

    @Query("SELECT * FROM bookmark WHERE isFavorite = 1 ORDER BY creation_date DESC")
    fun getFavoriteBookmarks(): PagingSource<Int, Bookmark>

    @Query("SELECT COUNT(*) FROM bookmark WHERE isFavorite = 1")
    fun countFavoriteBookmarks(): Flow<Int>

    @Query("SELECT * FROM bookmark WHERE openCount > 0 ORDER BY openCount DESC, last_opened DESC LIMIT :limit")
    fun getMostVisitedBookmarks(limit: Int = 50): PagingSource<Int, Bookmark>

    @Query("SELECT COUNT(*) FROM bookmark WHERE openCount > 0")
    fun countMostVisitedBookmarks(): Flow<Int>

    @Query("SELECT * FROM bookmark WHERE mediaType LIKE '%video%' ORDER BY creation_date DESC")
    fun getVideoBookmarks(): PagingSource<Int, Bookmark>

    @Query("SELECT COUNT(*) FROM bookmark WHERE mediaType LIKE '%video%'")
    fun countVideoBookmarks(): Flow<Int>

    @Query("SELECT * FROM bookmark WHERE last_opened > :timestamp ORDER BY last_opened DESC")
    fun getRecentlyViewedBookmarks(timestamp: Long): PagingSource<Int, Bookmark>

    @Query("SELECT COUNT(*) FROM bookmark WHERE last_opened > :timestamp")
    fun countRecentlyViewedBookmarks(timestamp: Long): Flow<Int>

    @Query("UPDATE bookmark SET isRead = :isRead WHERE rawUrl = :url")
    fun updateReadStatus(url: String, isRead: Boolean)

    @Query("UPDATE bookmark SET isFavorite = :isFavorite WHERE rawUrl = :url")
    fun updateFavoriteStatus(url: String, isFavorite: Boolean)

    @Query("UPDATE bookmark SET openCount = openCount + 1, last_opened = :timestamp WHERE rawUrl = :url")
    fun incrementOpenCount(url: String, timestamp: Long = System.currentTimeMillis())

}

data class BookmarkId(
    val id: Long
)
