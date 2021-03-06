package xyz.graphitenerd.tassel.di

import android.content.Context
import com.raqun.beaverlib.data.DataSource
import com.raqun.beaverlib.model.MetaData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.graphitenerd.tassel.data.AppDatabase
import xyz.graphitenerd.tassel.data.BookmarkDao
import xyz.graphitenerd.tassel.data.BookmarkLocalDataSource
import xyz.graphitenerd.tassel.model.Bookmark
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }
}
