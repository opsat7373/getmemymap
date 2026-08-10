package com.opsat.getmemymap.ui.region

import com.opsat.getmemymap.domain.model.DownloadState

data class RegionUIItem (
    val regionId: String,
    val regionName: String,
    val regionTranslatedName: String,
    val parentRegionName: String,
    val hasChild: Boolean,
    val canDownload : Boolean,
    val downloadProgress : Int,
    val state : DownloadState?,
    val downloadFileName: String?
)