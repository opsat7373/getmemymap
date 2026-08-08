package com.opsat.getmemymap.data.mapper

import com.opsat.getmemymap.data.local.database.DbDownloadState
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.data.local.xml.RegionXml
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.model.RegionInfoModel

fun RegionXml.toDomainModel() = RegionInfoModel(
    name = name,
    parentRegionName = parentRegionName,
    downloadFileName = "${downloadPrefix}${name}_europe_2.obf.zip".replaceFirstChar { it.uppercase() },
    hasChild = hasChild
)

fun DownloadEntity.toDomainModel() =
    RegionDownloadInfoModel(
        regionName = regionName,
        parentRegionName = parentRegionName,
        downloadUrl = downloadUrl,
        localFile = localFile,
        queuePosition = queuePosition,
        state = state.toDomain(),
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes
    )

fun DbDownloadState.toDomain() =
    when (this) {
        DbDownloadState.QUEUED -> DownloadState.QUEUED
        DbDownloadState.DOWNLOADING -> DownloadState.DOWNLOADING
        DbDownloadState.UNKNOWN -> DownloadState.UNKNOWN
        DbDownloadState.COMPLETED -> DownloadState.COMPLETED
        DbDownloadState.FAILED -> DownloadState.FAILED
}
