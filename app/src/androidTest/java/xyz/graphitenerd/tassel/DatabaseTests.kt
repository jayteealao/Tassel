package xyz.graphitenerd.tassel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.graphitenerd.tassel.data.AppDatabase
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.model.Bookmark
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseEntityReadWriteTest {
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        bookmarkDao = db.bookmarkDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val bookmark: Bookmark = TestUtil.createBookmarks()

        bookmarkDao.addBookmark(bookmark)
        val byRawUrl = bookmarkDao.getBookmark(bookmark.rawUrl)
        assertThat(byRawUrl[0], equalTo(bookmark))
    }
}

class TestUtil {
    companion object {
        fun createBookmarks(): Bookmark  = Bookmark(
            rawUrl = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier"
        )
    }
}