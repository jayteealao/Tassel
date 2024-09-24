package xyz.graphitenerd.tassel.service

import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder


interface StorageService {
    fun isUserSet(): Boolean

    fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Bookmark) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListener()
    suspend fun syncBookmarksToStorage(
        getLocalBookmark: (Long) -> List<Bookmark>,
        onError: (Throwable) -> Unit
    )

    suspend fun syncFoldersToCloud(
        folders: List<BookmarkFolder>
    )

    suspend fun saveBookmark(bookmark: Bookmark, onSuccess: (Bookmark) -> Unit = {})
//    fun getTask(taskId: String, onError: (Throwable) -> Unit, onSuccess: (Bookmark) -> Unit)
//    fun saveTask(task: Task, onResult: (Throwable?) -> Unit)
//    fun updateTask(task: Task, onResult: (Throwable?) -> Unit)
//    fun deleteTask(taskId: String, onResult: (Throwable?) -> Unit)
//    fun deleteAllForUser(userId: String, onResult: (Throwable?) -> Unit)
//    fun updateUserId(oldUserId: String, newUserId: String, onResult: (Throwable?) -> Unit)
    fun setUserId(userId: String)
    suspend fun saveFolder(folder: BookmarkFolder)
}
