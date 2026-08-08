package com.opsat.getmemymap.domain.model

import com.opsat.getmemymap.data.local.database.DbDownloadState

data class RegionDownloadInfoModel(val regionName : String,
                                   val parentRegionName: String,
                                   val downloadUrl : String? = null,
                                   val localFile: String? = null,
                                   val queuePosition: Long,
                                   val state: DownloadState,
                                   val downloadedBytes: Long,
                                   val totalBytes: Long)