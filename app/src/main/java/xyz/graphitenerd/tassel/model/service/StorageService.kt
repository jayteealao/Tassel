package xyz.graphitenerd.tassel.model.service

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
    fun setUserId(userId: String)
    suspend fun saveFolder(folder: BookmarkFolder)
}
