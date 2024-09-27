package xyz.graphitenerd.tassel.model

import android.webkit.URLUtil
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.Invalid
import io.github.boguszpawlowski.chassis.Valid
import io.github.boguszpawlowski.chassis.Validator
import xyz.graphitenerd.tassel.ui.FileTree

data class BookMarkForm(
    val title: Field<BookMarkForm, String?>,
    val address: Field<BookMarkForm, String>,
    val folderTree: Field<BookMarkForm, FileTree>
)

fun isValidURL() = Validator<String?> { value ->
    if (URLUtil.isValidUrl(value)) {
        Valid
    } else {
        InvalidURLInput(value)
    }
}

data class InvalidURLInput(val url: String?) : Invalid