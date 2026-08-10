package com.opsat.getmemymap.data.mapper

import com.opsat.getmemymap.data.local.database.DbDownloadState
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.data.local.xml.MapXml
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.MapDownloadingInfoModel
import com.opsat.getmemymap.domain.model.MapInfoModel

fun MapXml.toDomainModel() = MapInfoModel(
    mapId = "${parentMapName}_${name}",
    name = name,
    translate = translate,
    parentMapId = parentMapName,
    downloadFileName = "${downloadPrefix}${name}_europe_2.obf.zip".replaceFirstChar { it.uppercase() },
    hasChild = hasChild,
    downloadAvailable = downloadAvailable
)

fun DownloadEntity.toDomainModel() =
    MapDownloadingInfoModel(
        mapId = "${parentMapId}_${mapName}",
        mapName = mapName,
        parentMapName = parentMapId,
        downloadUrl = downloadUrl,
        localFile = localFile,
        queuePosition = queuePosition,
        state = state.toDomain(),
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes
    )

fun MapDownloadingInfoModel.toDbEntity() =
    DownloadEntity (
        mapId = "${parentMapName}_${mapName}",
        mapName = mapName,
        parentMapId = parentMapName,
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
