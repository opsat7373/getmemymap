package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.MapDownloadingInfoModel
import com.opsat.getmemymap.domain.model.MapInfoModel
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

    suspend fun addDownloadMap(region : MapInfoModel)

    suspend fun stopDownloadMap(region : MapInfoModel)

    fun getRegionDownloadInfo(parentRegionName : String?) : Flow<List<MapDownloadingInfoModel>>

    fun getEnqueuedDownloads() : MapDownloadingInfoModel?

    suspend fun update(downloadInfoModel : MapDownloadingInfoModel)

    fun updateState(regionId : String, state : DownloadState)

}