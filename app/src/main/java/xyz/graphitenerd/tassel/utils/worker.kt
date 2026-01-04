package xyz.graphitenerd.tassel.utils

import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import xyz.graphitenerd.tassel.model.BookmarkFolder


//@HiltWorker
// class SyncWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted params: WorkerParameters,
//    val bookmarkRepository: BookmarkRepository,
//    val storageService: StorageService,
//    val accountService: AccountService
// ) : CoroutineWorker(context, params) {
//    override suspend fun doWork(): Result {
//        if (!accountService.hasUser()) { return Result.failure() }
//        Log.e("syncworker", "userid: ${accountService.getUserId()}")
//
//        val job = SupervisorJob()
//        withContext(Dispatchers.IO + job) {
//            storageService.syncBookmarksToStorage(
//                userId = accountService.getUserId(),
//                getLocalBookmark = {
//                    bookmarkRepository.getLastSavedBookmark(it)
//                }
//            ) {
//                Log.e("syncworker", "${it.message}")
//                job.completeExceptionally(it)
//            }
//        }
//        job.join()
//        if (job.isCancelled) {
//            return Result.failure()
//        }
//        return Result.success()
//    }
//}

val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

//val periodicWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
//    .setConstraints(constraints)
//    .build()
//
//val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
//    .setConstraints(constraints)
////    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//    .build()

//
//fun loadJsonBookmarks() {
//    viewModelScope.launch(Dispatchers.IO) {
//        val data: BookmarkObject = parse(context)
//        log(data.toString())
//        val folders = retreiveFolders(data, "home")
//        log(folders.toString())
//        val bookmarks = retreiveBookmarks(data, "home")
//        log(bookmarks.toString())
//        val metadataToBookmarkMapper = MetadataToBookmarkMapper()
//        folders.forEach {
//            viewModelScope.launch(Dispatchers.IO) {
//                log(it.parent)
//                val parentId = repository
//                    .getFolderByName(
//                        if (it.parent == "bookmarks") "home" else it.parent
//                    ).id
//
//                repository.insertFolder(
//                    BookmarkFolder(
//                        name = it.folder.name,
//                        parentId = parentId
//                    )
//                )
//            }
//        }
//
//        bookmarks.forEach {
//            viewModelScope.launch(Dispatchers.IO) {
//                val metadata = Beaver.load(it.url).await()
//                if (Beaver.isInitialized() && metadata != null) {
//                    var bookmark = metadataToBookmarkMapper.map(metadata)
//                    repository.addBookmark(
//                        bookmark.copy(folderId = repository.getFolderByName(it.folder).id)
//                    )
//                }
//
//            }
//        }
//    }
//}