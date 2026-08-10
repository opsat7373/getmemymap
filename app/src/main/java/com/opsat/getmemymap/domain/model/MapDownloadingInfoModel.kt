package com.opsat.getmemymap.domain.model

data class MapDownloadingInfoModel(
    val regionId: String,
    val regionName : String,
    val parentRegionName: String,
    val downloadUrl : String? = null,
    val localFile: String? = null,
    val queuePosition: Long,
    val state: DownloadState,
    val downloadedBytes: Long,
    val totalBytes: Long)