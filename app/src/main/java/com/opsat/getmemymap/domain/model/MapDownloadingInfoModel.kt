package com.opsat.getmemymap.domain.model

data class MapDownloadingInfoModel(
    val mapId: String,
    val mapName : String,
    val parentMapName: String,
    val downloadUrl : String? = null,
    val localFile: String? = null,
    val queuePosition: Long,
    val state: DownloadState,
    val downloadedBytes: Long,
    val totalBytes: Long)