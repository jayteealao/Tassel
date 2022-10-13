package xyz.graphitenerd.tassel.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutineModule {

    @Singleton
    @Provides
    fun providesTasselDispatcherIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Singleton
    @Provides
    fun providesCoroutineScope(coroutineDispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(coroutineDispatcher)
    }
}