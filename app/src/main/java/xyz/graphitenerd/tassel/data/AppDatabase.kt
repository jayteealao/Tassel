package xyz.graphitenerd.tassel.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import xyz.graphitenerd.tassel.model.Bookmark
import java.net.URL
import java.util.concurrent.Executors

@Database(entities = [Bookmark::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { it ->
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "AppDatabase")
                .setQueryCallback(
                    object : RoomDatabase.QueryCallback {
                        override fun onQuery(sqlQuery: String, bindArgs: MutableList<Any>) {
                            Log.e("tassel","sqlquery: $sqlQuery")
                            Log.e("tassel","sqlquery Args: $bindArgs")
                        }
                    },
                    Executors.newSingleThreadExecutor()
                ).build()
        }
    }
}

class Converters {
    @TypeConverter
    fun urlToString(value: URL): String {
        return value.toString()
    }
    @TypeConverter
    fun stringToUrl(value: String): URL {
        return URL(value)
    }
}
