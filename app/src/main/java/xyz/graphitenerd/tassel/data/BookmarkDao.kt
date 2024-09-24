package xyz.graphitenerd.tassel.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.Tag
import xyz.graphitenerd.tassel.model.BookmarkTagCrossRef

@Dao
interface BookmarkDao {

    @Upsert
    fun addBookmark(bookmark: Bookmark): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: Bookmark): Long

    @Delete
    fun deleteBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmark WHERE rawUrl = :url")
    fun deleteBookmarkWithUrl(url: String)

    @Query("SELECT * FROM bookmark WHERE synced = false")
    fun getUnsyncedBookmarks(): List<Bookmark>

//    @Query("SELECT * FROM bookmark")
//    fun getAllBookmarks(): List<Bookmark>
//
//    @Query("SELECT * FROM bookmark")
//    fun getAllBookmarks(): PagingSource<Int, Bookmark>

    @Query("SELECT * FROM bookmark")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmark ORDER BY creation_date DESC LIMIT 20")
    fun getRecentBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmark WHERE creation_date > :time ORDER BY creation_date DESC")
    fun getLastSavedBookmark(time: Long): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getBookmarkById(id: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE rawUrl LIKE :search")
    fun getBookmark(search: String): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE folderId LIKE :folderId")
    fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>>

    @Query("SELECT count(*) FROM bookmark")
    fun countBookmarks(): Flow<Int>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTag(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmarkTagCrossRef(crossRef: BookmarkTagCrossRef): Long

    @Query("DELETE FROM BookmarkTagCrossRef WHERE bookmarkId = :bookmarkId AND tagId = :tagId")
    fun deleteTagFromBookmark(bookmarkId: Long, tagId: Long)

    @Query("""
        SELECT * FROM bookmark 
        INNER JOIN BookmarkTagCrossRef ON bookmark.id = BookmarkTagCrossRef.bookmarkId 
        WHERE BookmarkTagCrossRef.tagId = :tagId
    """)
    fun getBookmarksByTag(tagId: Long): Flow<List<Bookmark>>
}

