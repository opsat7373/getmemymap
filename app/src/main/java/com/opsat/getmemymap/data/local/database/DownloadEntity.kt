package com.opsat.getmemymap.data.local.database

import androidx.room.Entity

@Entity(
    tableName = "downloads",
    primaryKeys = ["regionId"]
)
data class DownloadEntity(
    val regionId : String,

    val parentRegionName: String,

    val regionName: String,

    val downloadUrl: String,

    val localFile: String,

    val queuePosition: Long,

    val state: DbDownloadState,

    val downloadedBytes: Long,

    val totalBytes: Long,
)