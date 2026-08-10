package com.opsat.getmemymap.di

import com.opsat.getmemymap.data.local.MapRepositoryImpl
import com.opsat.getmemymap.data.local.DownloadRepositoryImpl
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMapsRepository(
        impl: MapRepositoryImpl
    ): MapRepository

    @Binds
    @Singleton
    abstract fun bingDownloadRepository(
        impl: DownloadRepositoryImpl
    ): DownloadRepository
}