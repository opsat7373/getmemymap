package com.opsat.getmemymap.di

import android.content.Context
import androidx.room.Room
import com.opsat.getmemymap.data.local.database.AppDatabase
import com.opsat.getmemymap.data.local.database.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "map_file_database"
        ).build()
    }

    @Provides
    fun provideDownloadDao(
        database: AppDatabase
    ): DownloadDao {

        return database.downloadDao()
    }
}