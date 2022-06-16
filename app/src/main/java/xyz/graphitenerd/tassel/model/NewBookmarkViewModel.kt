package xyz.graphitenerd.tassel.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chimbori.crux.articles.ArticleExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.reduce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.jsoup.Jsoup
import xyz.graphitenerd.tassel.data.BookmarkRepository
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class NewBookmarkViewModel @Inject constructor(val bookmarkRepository: BookmarkRepository) : ViewModel() {

}


