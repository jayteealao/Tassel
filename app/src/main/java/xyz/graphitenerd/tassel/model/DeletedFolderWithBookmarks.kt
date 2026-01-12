package xyz.graphitenerd.tassel.model

/**
 * Represents a folder and its contents that were deleted, for undo support.
 *
 * @property folder The folder that was deleted
 * @property bookmarks The bookmarks that were in the folder
 * @property originalParentId The parent folder ID where the folder was originally located
 */
data class DeletedFolderWithBookmarks(
    val folder: BookmarkFolder,
    val bookmarks: List<Bookmark>,
    val originalParentId: Long?
)
