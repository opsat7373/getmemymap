package com.opsat.getmemymap.ui.map

import com.opsat.getmemymap.domain.model.DownloadState

data class MapUIItem (
    val mapId: String,
    val mapName: String,
    val mapTranslatedName: String,
    val parentMapId: String,
    val hasChild: Boolean,
    val canDownload : Boolean,
    val downloadProgress : Int,
    val state : DownloadState?,
    val downloadFileName: String?
)