package xyz.graphitenerd.tassel.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder


@Dao
interface FolderDao {
    @Insert
    fun insertFolder(folder: BookmarkFolder)

    @Delete
    fun deleteFolder(folder: BookmarkFolder)

    @Query("SELECT * FROM bookmark_folder WHERE parentId = :id")
    fun getFolderChildren(id: Long?): List<BookmarkFolder>

    @Query(
        "SELECT folder.id AS id, folder.name AS name, " +
                "child_folder.parentId AS parentFolderId, " +
                "bookmark.id AS bookmarkId, bookmark.url AS Url " +
                "FROM bookmark_folder AS folder, bookmark_folder as child_folder, bookmark " +
                "WHERE folder.id = child_folder.id AND folder.id = bookmark.folderId"
    )
    fun getFoldersAndBookmarks(): Map<BookmarkFolder, List<folderWithContents>>

@Query(
        "SELECT * FROM bookmark_folder " +
                "JOIN bookmark ON bookmark_folder.id = bookmark.folderId"
    )
    fun getFoldersAndBookmarksB(): Map<BookmarkFolder, List<Bookmark>>

    data class folderWithContents(
        val id: Long,
        val name: String,
        val parentFolderId: Long,
        val Url: String
    )
}