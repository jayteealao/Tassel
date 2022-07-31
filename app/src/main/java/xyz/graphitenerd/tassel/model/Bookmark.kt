package xyz.graphitenerd.tassel.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raqun.beaverlib.data.local.DbEntity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Bookmark(
    @PrimaryKey var rawUrl: String,
    var url: String? = null,
    var title: String? = null,
    var desc: String? = null,
    var imageUrl: String? = null,
    var name : String? = null,
    val mediaType: String? = null,
    var favIcon: String? = null,
) : Parcelable, DbEntity
