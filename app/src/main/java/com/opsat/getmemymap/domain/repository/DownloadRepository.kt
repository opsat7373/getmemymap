package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.model.RegionModel
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

    suspend fun addDownloadMap(region : RegionModel)

    suspend fun stopDownloadMap(region : RegionModel)

    fun getRegionDownloadInfo(parentRegionName : String?) : Flow<List<RegionDownloadInfoModel>>
}