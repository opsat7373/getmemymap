package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.model.RegionInfoModel
import com.opsat.getmemymap.domain.model.RegionModel
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

    suspend fun addDownloadMap(region : RegionInfoModel)

    suspend fun stopDownloadMap(region : RegionInfoModel)

    fun getRegionDownloadInfo(parentRegionName : String?) : Flow<List<RegionDownloadInfoModel>>

    fun getEnqueuedDownloads() : Flow<List<RegionDownloadInfoModel>>

    suspend fun updateDownload(downloadInfoModel: RegionDownloadInfoModel)

}