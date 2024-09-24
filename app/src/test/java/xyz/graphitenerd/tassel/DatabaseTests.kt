package xyz.graphitenerd.tassel

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import xyz.graphitenerd.tassel.data.AppDatabase
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.model.Bookmark
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
@Config(minSdk = Build.VERSION_CODES.S, maxSdk = Build.VERSION_CODES.S)
//@RunWith(RobolectricTestRunner::class)
class DatabaseEntityReadWriteTest {
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var db: AppDatabase

    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

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
    fun writeUserAndReadInList() = testScope.runTest {
        val bookmark: Bookmark = TestUtil.createBookmarks()
        testScope.launch(Dispatchers.IO) {
            bookmarkDao.addBookmark(bookmark)
            val byRawUrl = bookmarkDao.getBookmark(bookmark.rawUrl)
            assertThat(byRawUrl[0], equalTo(bookmark))
        }

    }
}

class TestUtil {
    companion object {
        fun createBookmarks(): Bookmark  = Bookmark(
            rawUrl = "https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier"
        )
    }
}