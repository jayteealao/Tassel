package xyz.graphitenerd.tassel.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.net.URL

@Parcelize
@Entity
data class Bookmark(
    @PrimaryKey val id: Int,
    val title: String?,
    val url: URL,
    val favicon: URL?,
    var imageURl: URL? = null
) : Parcelable
