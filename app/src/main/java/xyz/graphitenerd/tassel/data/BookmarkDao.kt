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

}

data class BookmarkId(
    val id: Long
)
