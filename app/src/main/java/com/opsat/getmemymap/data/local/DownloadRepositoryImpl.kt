package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.data.local.database.DownloadDao
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.data.local.database.DbDownloadState
import com.opsat.getmemymap.data.mapper.toDbEntity
import com.opsat.getmemymap.data.mapper.toDomainModel
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.model.RegionInfoModel
import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(

    private val downloadDao: DownloadDao
) : DownloadRepository {
    override suspend fun addDownloadMap(region: RegionInfoModel) {
        val downloadFileName = region.downloadFileName
        if (downloadFileName != null) {
            val downloadEntity = DownloadEntity(
                region.regionId,
                region.parentRegionName,
                region.name,
                downloadFileName,
                downloadFileName,
                -1,
                DbDownloadState.QUEUED,
                -1,
                -1

            )
            downloadDao.insertWithNextQueue(downloadEntity)
        }
    }

    override suspend fun stopDownloadMap(region: RegionInfoModel) {
        val downloadFileName = region.downloadFileName
        if (downloadFileName != null) {
            val downloadEntity = DownloadEntity(
                region.regionId,
                region.parentRegionName,
                region.name,
                downloadFileName,
                downloadFileName,
                -1,
                DbDownloadState.UNKNOWN,
                -1,
                -1

            )
            downloadDao.delete(downloadEntity)
        }
    }

    override fun getRegionDownloadInfo(parentRegionName: String?): Flow<List<RegionDownloadInfoModel>> =
        downloadDao.observeRegionsByParentName(parentRegionName).map { list ->
            list.map { regionEntity -> regionEntity.toDomainModel()
        }
    }

    override fun getEnqueuedDownloads(): RegionDownloadInfoModel? =
        downloadDao.getQueued()?.toDomainModel()

    override suspend fun update(downloadInfoModel: RegionDownloadInfoModel) {
        return downloadDao.update(downloadInfoModel.toDbEntity())
    }

    override fun updateState(regionId : String, state : DownloadState) {
        downloadDao
            .updateState(regionId, state.toDbEntity().toString())
    }
}