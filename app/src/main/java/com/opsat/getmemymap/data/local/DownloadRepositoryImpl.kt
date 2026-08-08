package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.data.local.database.DownloadDao
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.data.local.database.DbDownloadState
import com.opsat.getmemymap.data.mapper.toDomainModel
import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(

    private val downloadDao: DownloadDao
) : DownloadRepository {
    override suspend fun addDownloadMap(region: RegionModel) {
        val downloadFileName = region.downloadFileName
        if (downloadFileName != null) {
            val downloadEntity = DownloadEntity(
                region.name,
                region.parentRegionName,
                region.name,
                downloadFileName,
                "",
                -1,
                DbDownloadState.QUEUED,
                -1,
                -1

            )
            downloadDao.insertWithNextQueue(downloadEntity)
        }
    }

    override fun getRegionDownloadInfo(parentRegionName: String?): Flow<List<RegionDownloadInfoModel>> =
        downloadDao.observeRegionsByParentName(parentRegionName).map { list ->
            list.map { regionEntity -> regionEntity.toDomainModel()
        }
    }
}