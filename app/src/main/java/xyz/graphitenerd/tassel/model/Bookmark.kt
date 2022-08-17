package xyz.graphitenerd.tassel.model

import android.os.Parcelable
import android.webkit.URLUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raqun.beaverlib.data.local.DbEntity
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.Invalid
import io.github.boguszpawlowski.chassis.Valid
import io.github.boguszpawlowski.chassis.Validator
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Bookmark(
    @PrimaryKey var rawUrl: String,
    var url: String? = null,
    var title: String? = null,
    var desc: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    val mediaType: String? = null,
    var favIcon: String? = null,
) : Parcelable, DbEntity, BookmarkMarker

object EmptyBookmark : BookmarkMarker
sealed interface BookmarkMarker

data class BookMarkForm(
    val title: Field<BookMarkForm, String?>,
    val address: Field<BookMarkForm, String>
)

fun isValidURL() = Validator<String?> { value ->
    if (URLUtil.isValidUrl(value)) {
        Valid
    } else {
        InvalidURLInput(value)
    }
}

data class InvalidURLInput(val url: String?) : Invalid
