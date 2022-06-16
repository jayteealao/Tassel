package xyz.graphitenerd.tassel.data

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.graphitenerd.tassel.model.Bookmark

@Dao
interface BookmarkDao {

    @Insert
    fun addBookmark(bookmark: Bookmark)

    @Delete
    fun deleteBookmark(bookmark: Bookmark)

//    @Query("SELECT * FROM bookmark")
//    fun getAllBookmarks(): List<Bookmark>
//
    @Query("SELECT * FROM bookmark")
    fun getAllBookmarks(): PagingSource<Int, Bookmark>

    @Query("SELECT count(*) FROM bookmark")
    fun countBookmarks(): Flow<Int>

}
