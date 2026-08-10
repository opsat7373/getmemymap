package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.MapDownloadingInfoModel
import com.opsat.getmemymap.domain.model.MapInfoModel
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

    suspend fun addDownloadMap(mapInfo : MapInfoModel)

    suspend fun stopDownloadMap(mapInfo : MapInfoModel)

    fun getMapDownloadInfo(parentMapId : String?) : Flow<List<MapDownloadingInfoModel>>

    fun getEnqueuedDownloads() : MapDownloadingInfoModel?

    suspend fun update(downloadInfoModel : MapDownloadingInfoModel)

    fun updateState(mapId : String, state : DownloadState)

}