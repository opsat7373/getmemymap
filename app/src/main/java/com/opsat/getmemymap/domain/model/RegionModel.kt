package com.opsat.getmemymap.domain.model

data class RegionModel (
    val regionId: String,
    val name : String,
    val translate : String,
    val parentRegionName: String,
    val downloadFileName: String? = null,
    val hasChild: Boolean,
    val downloadAvailable: Boolean,
    val downloadInfo: RegionDownloadInfoModel? = null
)