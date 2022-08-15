package xyz.graphitenerd.tassel.data

import android.util.Log
import com.raqun.beaverlib.data.DataSource
import com.raqun.beaverlib.model.MetaData
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import xyz.graphitenerd.tassel.MainApplication
import javax.inject.Inject


// an implementation of beavers Datasource.Local interface over my bookmarks db
class BookmarkLocalDataSource @Inject constructor() : DataSource.Local<String, MetaData> {

    @Inject
    lateinit var bookmarkDao: BookmarkDao

    init {
        val bookmarkDaoEPFactory = EntryPointAccessors.fromApplication(
            MainApplication.instance.applicationContext,
            BookmarkDaoEntryPoint::class.java)

        bookmarkDao = bookmarkDaoEPFactory.bookmarkDao
    }

    private val mapper = MetadataToBookmarkMapper()
    override fun get(key: String): MetaData? {
        val bookmarkList = bookmarkDao.getBookmark(key)
        return if (bookmarkList.isEmpty()) {
            null
        } else {
            mapper.map(bookmarkList[0])
        }
    }

    override fun put(key: String, data: MetaData): Boolean {
        Log.e("tassel", "in tassel local")
        val data = mapper.map(data)
        return if (get(key) == null) {
            val rowid = bookmarkDao.addBookmark(data)
            Log.e("tassel", "in tassel local; rowid $rowid")
            return rowid > 0
        } else { false }
    }

    override fun remove(key: String): Boolean {
        bookmarkDao.deleteBookmarkWithUrl(key)
        return true
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BookmarkDaoEntryPoint {

    val bookmarkDao: BookmarkDao

}