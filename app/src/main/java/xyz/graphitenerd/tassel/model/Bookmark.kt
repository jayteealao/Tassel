package xyz.graphitenerd.tassel.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = BookmarkFolder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("folderId")
        )
    ],
    primaryKeys = ["rawUrl"]
)

data class Bookmark(
    var id: Long = UUID.randomUUID().mostSignificantBits,
    var rawUrl: String,
    var url: String? = null,
    var title: String? = null,
    var desc: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    var mediaType: String? = null,
    var favIcon: String? = null,
    var folderId: Long? = null,
    @ColumnInfo(name = "creation_date")
    var creationDate: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = true.toString())
    var synced: Boolean = false
) : BookmarkMarker

object EmptyBookmark : BookmarkMarker
sealed interface BookmarkMarker