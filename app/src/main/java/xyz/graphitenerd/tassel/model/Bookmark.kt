package xyz.graphitenerd.tassel.model

import android.os.Parcelable
import android.webkit.URLUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.raqun.beaverlib.data.local.DbEntity
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.Invalid
import io.github.boguszpawlowski.chassis.Valid
import io.github.boguszpawlowski.chassis.Validator
import kotlinx.parcelize.Parcelize
import xyz.graphitenerd.tassel.ui.Folder

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = BookmarkFolder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("folderId")
        )
    ]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(defaultValue = "0") var id: Long = 0,
    var rawUrl: String,
    var url: String? = null,
    var title: String? = null,
    var desc: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    val mediaType: String? = null,
    var favIcon: String? = null,
    var folderId: Long? = null
) : Parcelable, DbEntity, BookmarkMarker

object EmptyBookmark : BookmarkMarker
sealed interface BookmarkMarker

data class BookMarkForm(
    val title: Field<BookMarkForm, String?>,
    val address: Field<BookMarkForm, String>,
    val folder: Field<BookMarkForm, Folder>
)

fun isValidURL() = Validator<String?> { value ->
    if (URLUtil.isValidUrl(value)) {
        Valid
    } else {
        InvalidURLInput(value)
    }
}

data class InvalidURLInput(val url: String?) : Invalid

@Entity(
    tableName = "bookmark_folder",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = BookmarkFolder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("parentId")
        )
    )
)
data class BookmarkFolder(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var parentId: Long?
)
