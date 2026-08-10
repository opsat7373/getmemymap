package com.opsat.getmemymap.data.mapper

import com.opsat.getmemymap.data.local.database.DbDownloadState
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.data.local.xml.RegionXml
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.model.RegionInfoModel

fun RegionXml.toDomainModel() = RegionInfoModel(
    regionId = "${parentRegionName}_${name}",
    name = name,
    translate = translate,
    parentRegionName = parentRegionName,
    downloadFileName = "${downloadPrefix}${name}_europe_2.obf.zip".replaceFirstChar { it.uppercase() },
    hasChild = hasChild,
    downloadAvailable = downloadAvailable
)

fun DownloadEntity.toDomainModel() =
    RegionDownloadInfoModel(
        regionId = "${parentRegionName}_${regionName}",
        regionName = regionName,
        parentRegionName = parentRegionName,
        downloadUrl = downloadUrl,
        localFile = localFile,
        queuePosition = queuePosition,
        state = state.toDomain(),
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes
    )

fun RegionDownloadInfoModel.toDbEntity() =
    DownloadEntity (
        regionId = "${parentRegionName}_${regionName}",
        regionName = regionName,
        parentRegionName = parentRegionName,
        downloadUrl = downloadUrl ?: "",
        localFile = localFile ?: "",
        queuePosition = queuePosition,
        state = state.toDbEntity(),
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes
    )

fun DbDownloadState.toDomain() =
    when (this) {
        DbDownloadState.QUEUED -> DownloadState.QUEUED
        DbDownloadState.DOWNLOADING -> DownloadState.DOWNLOADING
        DbDownloadState.SUSPENDED -> DownloadState.SUSPENDED
        DbDownloadState.COMPLETED -> DownloadState.COMPLETED
        DbDownloadState.FAILED -> DownloadState.FAILED
}

fun DownloadState.toDbEntity() =
    when (this) {
        DownloadState.QUEUED -> DbDownloadState.QUEUED
        DownloadState.DOWNLOADING -> DbDownloadState.DOWNLOADING
        DownloadState.SUSPENDED -> DbDownloadState.SUSPENDED
        DownloadState.COMPLETED -> DbDownloadState.COMPLETED
        DownloadState.FAILED -> DbDownloadState.FAILED
    }
