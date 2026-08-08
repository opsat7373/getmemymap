package com.opsat.getmemymap.data.local.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "downloads",
    indices = [
        Index("state"),
        Index("queuePosition")
    ],
    primaryKeys = ["parentRegionName", "regionName"]
)
data class DownloadEntity(

    val parentRegionName: String,

    val regionName: String,

    val downloadUrl: String,

    val localFile: String,

    val queuePosition: Long,

    val state: DbDownloadState,

    val downloadedBytes: Long,

    val totalBytes: Long,
)