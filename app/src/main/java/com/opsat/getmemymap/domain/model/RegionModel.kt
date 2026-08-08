package com.opsat.getmemymap.domain.model

data class RegionModel (val name : String,
                        val parentRegionName: String?,
                        val downloadFileName: String? = null,
                        val hasChild: Boolean,
                        val state: DownloadState,
                        val downloadedBytes: Long,
                        val totalBytes: Long)