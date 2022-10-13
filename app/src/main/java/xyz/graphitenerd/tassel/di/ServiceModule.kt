package xyz.graphitenerd.tassel.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import xyz.graphitenerd.tassel.model.service.AccountService
import xyz.graphitenerd.tassel.model.service.AccountServiceImpl
import xyz.graphitenerd.tassel.model.service.StorageService
import xyz.graphitenerd.tassel.model.service.StorageServiceImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class ServiceModule {
//    @Binds
//    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

//    @Binds
//    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
}


