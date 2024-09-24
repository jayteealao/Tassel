package xyz.graphitenerd.tassel

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import xyz.graphitenerd.tassel.data.AppDatabase
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.data.BookmarkRepository
import xyz.graphitenerd.tassel.data.IBookmarkRepository
import xyz.graphitenerd.tassel.data.repository.FolderRepository
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.screens.recents.BookmarkViewModel
import xyz.graphitenerd.tassel.service.AccountServiceImpl
import xyz.graphitenerd.tassel.service.StorageServiceImpl
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
@Config(minSdk = Build.VERSION_CODES.S, maxSdk = Build.VERSION_CODES.S)
//@RunWith(RobolectricTestRunner::class)
open class BookmarkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var db: AppDatabase
    private lateinit var repository: IBookmarkRepository
    private lateinit var folderRepository: FolderRepository
    private val storageService = mock(StorageServiceImpl::class.java)
    private val accountService = mock(AccountServiceImpl::class.java)
    private lateinit var viewModel: BookmarkViewModel

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = mainDispatcherRule.testDispatcher
    private val testScope = TestScope(testDispatcher)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        bookmarkDao = db.bookmarkDao()
        repository = BookmarkRepository(
            bookmarkDao = bookmarkDao,
            storageService = storageService,
            accountService = accountService,
            scope = testScope
        )

        folderRepository = FolderRepository(
            folderDao = db.folderDao(),
            storageService = storageService,
            accountService = accountService,
            scope = testScope
        )
        viewModel = BookmarkViewModel(
            bookmarkRepository = repository as BookmarkRepository,
            folderRepository = folderRepository,
            storageService = storageService,
            accountService = accountService,
            coroutineDispatcher = testDispatcher
        )

        runTest {
//            launch(Dispatchers.IO) {
                db.folderDao().insertFolder(
                    BookmarkFolder(
                        id = 1,
                        name = "HOME",
                        parentId = null
                    )
                )
                folders.forEach {
                    db.folderDao().insertFolder(it)
                }
//            }
//            advanceUntilIdle()
//            bookmarks.forEach {
////                this.launch(Dispatchers.IO) {
//                    bookmarkDao.insertBookmark(it)
//                }
            advanceUntilIdle()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `test that repository makes call to isUserSet`() = testScope.runTest {
        verify(storageService, atLeastOnce()).isUserSet()
    }

    @Test
    fun `test bookmark count flow`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.bookmarkCount.collect()
        }

        assertEquals(
            0,
            viewModel.bookmarkCount.value
        )

        viewModel.addBookmark(bookmarks[0])
//        advanceUntilIdle()
//        assertEquals(
//            1,
//            viewModel.bookmarkCount.value,
//        )
        viewModel.bookmarks.collectLatest {
            assertEquals(
                it[0].rawUrl.isNotEmpty(),
                true
            )
        }
    }
}