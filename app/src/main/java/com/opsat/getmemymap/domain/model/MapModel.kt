package com.opsat.getmemymap.domain.model

data class MapModel (
    val mapId: String,
    val name : String,
    val translate : String,
    val parentMapName: String,
    val downloadFileName: String? = null,
    val hasChild: Boolean,
    val downloadAvailable: Boolean,
    val downloadInfo: MapDownloadingInfoModel? = null
)