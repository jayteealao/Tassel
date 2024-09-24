package xyz.graphitenerd.tassel.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val scope: CoroutineScope
) : IBookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    override fun countBookmarks() = bookmarkDao.countBookmarks()

    override fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)

    override fun getRecentBookmarks() = bookmarkDao.getRecentBookmarks()

    override fun getBookmarkById(id: Long) = bookmarkDao.getBookmarkById(id)

//    fun deleteBookmarkById(id: Long) = bookmarkDao.deleteBookmarkById(id)

    override fun getLastSavedBookmark(time: Long) = bookmarkDao.getLastSavedBookmark(time)

    override fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)

    override fun getBookmarksByFolders(id: Long) = bookmarkDao.getBookmarksByFolder(id)

    init {
        if (accountService.hasUser() and !storageService.isUserSet()) {
//            Log.d("foldervm", "user will be set")
            storageService.setUserId(accountService.getUserId())
        }
    }

    override fun syncBookmarksToCloud() {
        if (!accountService.hasUser()) { return }
        Log.e("sync", "userid: ${accountService.getUserId()}")

        val job = SupervisorJob()
        scope.launch(Dispatchers.IO + job) {
            storageService.syncBookmarksToStorage(
                getLocalBookmark = {
                    getLastSavedBookmark(it)
                }
            ) {
                Log.e("sync", "${it.message}")
                job.completeExceptionally(it)
            }
        }
    }

    override suspend fun saveBookmarkToCloud(bookmark: Bookmark) = storageService
        .saveBookmark(bookmark.copy(synced = true)) {
            Log.d("sync-unsynced", "synced $it")
            scope.launch(Dispatchers.IO) {
                val data = bookmarkDao.addBookmark(it)
                Log.d("sync-unsynced", "synced $data")

            }
        }

    override suspend fun saveAndSyncBookmark(bookmark: Bookmark) {
        bookmarkDao.addBookmark(bookmark)
        saveBookmarkToCloud(bookmark)
    }

    override suspend fun syncUnsyncedBookmarks() {
        delay(1000)
        Log.d("sync-unsynced", "syncing")
        bookmarkDao.getUnsyncedBookmarks().forEach {
            Log.d("sync-unsynced", "$it")
            scope.launch {
                saveBookmarkToCloud(it)
            }
        }

    }
}
