package com.opsat.getmemymap.di

import com.opsat.getmemymap.domain.repository.DownloadScheduler
import com.opsat.getmemymap.service.WorkManagerDownloadScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadSchedulerModule {
    @Binds
    @Singleton
    abstract fun downloadScheduler(
        impl: WorkManagerDownloadScheduler
    ): DownloadScheduler
}