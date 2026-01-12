package xyz.graphitenerd.tassel.model

/**
 * Represents the state of folders that were moved, for undo support.
 *
 * @property folderIds The IDs of the folders that were moved
 * @property fromParentId The original parent folder ID
 * @property toParentId The destination parent folder ID
 */
data class MovedFolderState(
    val folderIds: List<Long>,
    val fromParentId: Long?,
    val toParentId: Long
)
