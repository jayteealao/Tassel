package xyz.graphitenerd.tassel.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.raqun.beaverlib.data.local.DbEntity
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
    val mediaType: String? = null,
    var favIcon: String? = null,
    var tags: List<String> = emptyList(),
    var folderId: Long? = null,
    @ColumnInfo(name = "creation_date")
    var creationDate: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = true.toString())
    var synced: Boolean = false
) : DbEntity, BookmarkMarker

object EmptyBookmark : BookmarkMarker
sealed interface BookmarkMarker

