package xyz.graphitenerd.tassel.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "bookmark_folder",
    foreignKeys = [ForeignKey(
        entity = BookmarkFolder::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parentId")
    )]
)
data class BookmarkFolder(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var parentId: Long?
)