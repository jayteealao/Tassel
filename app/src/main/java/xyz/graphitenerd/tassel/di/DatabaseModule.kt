package xyz.graphitenerd.tassel.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.graphitenerd.tassel.data.AppDatabase
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.data.FolderDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "AppDatabase"
    ).addCallback(
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO bookmark_folder values ( 1, 'home', NULL);")
            }
        }
    ).build()
    @Provides
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }

    @Provides
    fun provideFolderDao(appDatabase: AppDatabase): FolderDao = appDatabase.folderDao()
}
