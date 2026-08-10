package com.opsat.getmemymap.data.local.database

import androidx.room.Dao
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

    @Query("""
        DELETE FROM downloads
        WHERE mapId = :mapId
    """)
    suspend fun delete(mapId: String)

    @Query("""
        UPDATE downloads
        SET state = :state
        WHERE mapId = :mapId
    """)
    fun updateState(mapId: String, state: String)

    @Query("""
        SELECT *
        FROM downloads
        WHERE parentMapId = :parentMapId
    """)
    fun observeMapsByParentName(
        parentMapId: String?
    ): Flow<List<DownloadEntity>>

    @Query("""
        SELECT *
        FROM downloads
        WHERE state = 'QUEUED' OR state = 'DOWNLOADING' OR state = 'SUSPENDED'
        ORDER BY queuePosition
        LIMIT 1
    """)
    fun getQueued(): DownloadEntity?
}