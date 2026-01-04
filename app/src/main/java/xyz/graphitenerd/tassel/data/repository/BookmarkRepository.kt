package xyz.graphitenerd.tassel.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.data.BookmarkId
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.SmartCollection
import xyz.graphitenerd.tassel.model.SmartCollectionWithCount
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import java.util.concurrent.TimeUnit
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

    override fun searchBookmarks(query: String): List<Bookmark> = bookmarkDao.searchBookmarks(query)

    override fun countBookmarks() = bookmarkDao.countBookmarks()

    override fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)

    override fun addBookmarks(bookmarks: List<Bookmark>) = bookmarkDao.addBookmarks(bookmarks)

    override fun getRecentBookmarks() = bookmarkDao.getRecentBookmarks()

    override fun getBookmarkById(id: Long) = bookmarkDao.getBookmarkById(id)

    override fun getBookmarksById(ids: List<Long>) = bookmarkDao.getBookmarksById(ids)

//    fun deleteBookmarkById(id: Long) = bookmarkDao.deleteBookmarkById(id)

    override fun getLastSavedBookmark(time: Long) = bookmarkDao.getLastSavedBookmark(time)

    override fun deleteBookmark(bookmarks: List<Bookmark>) = bookmarkDao.deleteBookmark(bookmarks)

    override fun deleteBookmark(bookmarkIds: Array<BookmarkId>) = bookmarkDao.deleteBookmark(*bookmarkIds)

    override fun getBookmarksByFolders(id: Long) = bookmarkDao.getBookmarksByFolder(id)

    init {
        if (accountService.hasUser() and !storageService.isUserSet()) {
//            Log.d("foldervm", "user will be set")
            storageService.setUserId(accountService.getUserId())
        }
    }

    override fun syncBookmarksToCloud() {
        if (!accountService.hasUser()) { return }

        val job = SupervisorJob()
        scope.launch(Dispatchers.IO + job) {
            storageService.syncBookmarksToFirebase(
                getLocalBookmark = {
                    getLastSavedBookmark(it)
                }
            ) {
//                Log.e("sync", "${it.message}")
                job.completeExceptionally(it)
            }
        }
    }

    override suspend fun saveBookmarkToCloud(bookmark: Bookmark) = storageService
        .saveBookmark(bookmark.copy(synced = true)) {
            scope.launch(Dispatchers.IO) {
                bookmarkDao.addBookmark(it)
            }
        }

    override suspend fun saveAndSyncBookmark(bookmark: Bookmark) {
        bookmarkDao.addBookmark(bookmark)
        saveBookmarkToCloud(bookmark)
    }

    override suspend fun syncUnsyncedBookmarks() {
        delay(1000)
//        Log.d("sync-unsynced", "syncing")
        bookmarkDao.getUnsyncedBookmarks().forEach {
//            Log.d("sync-unsynced", "$it")
            scope.launch {
                saveBookmarkToCloud(it)
            }
        }

    }

    override fun bookmarksPagingData(): Flow<PagingData<Bookmark>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { bookmarkDao.bookmarksPagingSource() }
        ).flow
    }

    // Smart Collections Implementation

    override fun getSmartCollectionBookmarks(collection: SmartCollection): Flow<PagingData<Bookmark>> {
        val pagingSourceFactory = when (collection) {
            is SmartCollection.ReadLater -> {
                { bookmarkDao.getUnreadBookmarks() }
            }
            is SmartCollection.RecentlyAdded -> {
                val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
                { bookmarkDao.getRecentlyAddedBookmarks(sevenDaysAgo) }
            }
            is SmartCollection.Favorites -> {
                { bookmarkDao.getFavoriteBookmarks() }
            }
            is SmartCollection.MostVisited -> {
                { bookmarkDao.getMostVisitedBookmarks() }
            }
            is SmartCollection.Videos -> {
                { bookmarkDao.getVideoBookmarks() }
            }
            is SmartCollection.RecentlyViewed -> {
                val oneDayAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
                { bookmarkDao.getRecentlyViewedBookmarks(oneDayAgo) }
            }
        }

        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getSmartCollectionCount(collection: SmartCollection): Flow<Int> {
        return when (collection) {
            is SmartCollection.ReadLater -> bookmarkDao.countUnreadBookmarks()
            is SmartCollection.RecentlyAdded -> {
                val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
                bookmarkDao.countRecentlyAddedBookmarks(sevenDaysAgo)
            }
            is SmartCollection.Favorites -> bookmarkDao.countFavoriteBookmarks()
            is SmartCollection.MostVisited -> bookmarkDao.countMostVisitedBookmarks()
            is SmartCollection.Videos -> bookmarkDao.countVideoBookmarks()
            is SmartCollection.RecentlyViewed -> {
                val oneDayAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
                bookmarkDao.countRecentlyViewedBookmarks(oneDayAgo)
            }
        }
    }

    override fun getSmartCollectionsWithCounts(): Flow<List<SmartCollectionWithCount>> {
        val collections = SmartCollection.getFeatured()
        val countFlows = collections.map { collection ->
            getSmartCollectionCount(collection)
        }

        return combine(countFlows) { counts ->
            collections.mapIndexed { index, collection ->
                SmartCollectionWithCount(collection, counts[index])
            }
        }
    }

    override fun updateReadStatus(url: String, isRead: Boolean) {
        scope.launch(Dispatchers.IO) {
            bookmarkDao.updateReadStatus(url, isRead)
        }
    }

    override fun updateFavoriteStatus(url: String, isFavorite: Boolean) {
        scope.launch(Dispatchers.IO) {
            bookmarkDao.updateFavoriteStatus(url, isFavorite)
        }
    }

    override fun incrementOpenCount(url: String, timestamp: Long) {
        scope.launch(Dispatchers.IO) {
            bookmarkDao.incrementOpenCount(url, timestamp)
        }
    }
}
