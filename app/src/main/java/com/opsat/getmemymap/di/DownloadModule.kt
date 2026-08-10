package com.opsat.getmemymap.di;

import android.content.Context
import androidx.work.WorkManager
import com.opsat.getmemymap.data.downloader.DownloadController
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDownloadController(
        client: OkHttpClient
    ): DownloadController {
        return DownloadController(client)
    }
}
