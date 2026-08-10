package com.opsat.getmemymap.data.local.database

import androidx.room.Entity

@Entity(
    tableName = "downloads",
    primaryKeys = ["mapId"]
)
data class DownloadEntity(
    val mapId : String,

    val parentMapId: String,

    val mapName: String,

    val downloadUrl: String,

    val localFile: String,

    val queuePosition: Long,

    val state: DbDownloadState,

    val downloadedBytes: Long,

    val totalBytes: Long,
)