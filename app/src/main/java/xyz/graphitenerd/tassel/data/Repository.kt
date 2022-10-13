package xyz.graphitenerd.tassel.data

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.model.service.AccountService
import xyz.graphitenerd.tassel.model.service.StorageService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val folderDao: FolderDao,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val scope: CoroutineScope
) {
    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    fun countBookmarks() = bookmarkDao.countBookmarks()

    fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)

    fun getRecentBookmarks() = bookmarkDao.getRecentBookmarks()

    fun getBookmarkById(id: Long) = bookmarkDao.getBookmarkById(id)

//    fun deleteBookmarkById(id: Long) = bookmarkDao.deleteBookmarkById(id)

    fun getLastSavedBookmark(time: Long) = bookmarkDao.getLastSavedBookmark(time)

    fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)

    fun getFolderById(id: Long) = folderDao.getFolderById(id)

    fun getFoldersByParentId(id: Long? = null) = folderDao.getFolderChildren(id)

    fun getFolderByName(name: String) = folderDao.getFolderByName(name)

    fun getBookmarksByFolders(id: Long) = bookmarkDao.getBookmarksByFolder(id)

    fun insertFolder(folder: BookmarkFolder) = folderDao.insertFolder(folder)

    fun getFolders() = folderDao.getFolders()

    init {
        if (accountService.hasUser() and !storageService.isUserSet()) {
            Log.d("foldervm", "user will be set")
            storageService.setUserId(accountService.getUserId())
        }
    }
    fun syncFoldersToCloud() {
        if (!accountService.hasUser()) return
        scope.launch(Dispatchers.IO) {
            val folders = getFolders()
            storageService.syncFoldersToCloud(folders)
        }
    }

    fun syncBookmarksToCloud() {
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

    suspend fun saveBookmarkToCloud(bookmark: Bookmark) = storageService
        .saveBookmark(bookmark.copy(synced = true)) {
            Log.d("sync-unsynced", "synced $it")
            scope.launch(Dispatchers.IO) {
                val data = bookmarkDao.addBookmark(it)
                Log.d("sync-unsynced", "synced $data")

            }
        }

    suspend fun saveAndSyncBookmark(bookmark: Bookmark) {
        bookmarkDao.addBookmark(bookmark)
        saveBookmarkToCloud(bookmark)
    }

    suspend fun saveAndSyncFolder(folder: BookmarkFolder) {
        insertFolder(folder)
        storageService.saveFolder(folder)
    }

    suspend fun syncUnsyncedBookmarks() {
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
