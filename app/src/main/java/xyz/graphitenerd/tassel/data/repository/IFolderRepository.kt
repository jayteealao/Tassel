package xyz.graphitenerd.tassel.data.repository

import xyz.graphitenerd.tassel.model.BookmarkFolder

interface IFolderRepository {
    fun getFolderById(id: Long): BookmarkFolder
    fun getFoldersByParentId(id: Long?): List<BookmarkFolder>
    fun getFolderByName(name: String): BookmarkFolder
    fun insertFolder(folder: BookmarkFolder)
    fun getFolders(): List<BookmarkFolder>
    fun syncFoldersToCloud()

    suspend fun saveAndSyncFolder(folder: BookmarkFolder)
}