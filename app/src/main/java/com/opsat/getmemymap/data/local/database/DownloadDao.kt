package com.opsat.getmemymap.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT COALESCE(MAX(queuePosition), 0) + 1 FROM downloads")
    suspend fun nextQueue(): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(download: DownloadEntity): Long

    @Transaction
    suspend fun insertWithNextQueue(entity: DownloadEntity) {
        insert(
            entity.copy(
                queuePosition = nextQueue()
            )
        )
    }

    @Update
    suspend fun update(download: DownloadEntity)

    @Delete
    suspend fun delete(download: DownloadEntity)

    @Query("""
        SELECT *
        FROM downloads
        ORDER BY queuePosition
    """)
    fun observeQueue(): Flow<List<DownloadEntity>>

    @Query("""
        SELECT *
        FROM downloads
        WHERE state = 'DOWNLOADING'
        LIMIT 1
    """)
    suspend fun getCurrentDownload(): DownloadEntity?

    @Query("""
        SELECT *
        FROM downloads
        WHERE parentRegionName = :name
    """)
    fun observeRegionsByParentName(
        name: String?
    ): Flow<List<DownloadEntity>>

    @Query("""
        SELECT *
        FROM downloads
        WHERE state = 'QUEUED'
        ORDER BY queuePosition
        LIMIT 1
    """)
    suspend fun getNextInQueue(): DownloadEntity?

    @Query("""
        UPDATE downloads
        SET
            state = 'QUEUED'
        WHERE state = 'DOWNLOADING'
    """)
    suspend fun restoreInterruptedDownloads()
}