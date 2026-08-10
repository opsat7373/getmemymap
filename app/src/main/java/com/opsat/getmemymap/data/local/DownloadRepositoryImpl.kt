package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.data.local.database.DownloadDao
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.data.local.database.DbDownloadState
import com.opsat.getmemymap.data.mapper.toDbEntity
import com.opsat.getmemymap.data.mapper.toDomainModel
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.MapDownloadingInfoModel
import com.opsat.getmemymap.domain.model.MapInfoModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(

    private val downloadDao: DownloadDao
) : DownloadRepository {
    override suspend fun addDownloadMap(mapInfo: MapInfoModel) {
        val downloadFileName = mapInfo.downloadFileName
        if (downloadFileName != null) {
            val downloadEntity = DownloadEntity(
                mapInfo.mapId,
                mapInfo.parentMapId,
                mapInfo.name,
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

    override suspend fun stopDownloadMap(mapInfo: MapInfoModel) {
        downloadDao.delete(mapInfo.mapId)
    }

    override fun getMapDownloadInfo(parentMapId: String?): Flow<List<MapDownloadingInfoModel>> =
        downloadDao.observeMapsByParentName(parentMapId).map { list ->
            list.map { mapEntity -> mapEntity.toDomainModel()
        }
    }

    override fun getEnqueuedDownloads(): MapDownloadingInfoModel? =
        downloadDao.getQueued()?.toDomainModel()

    override suspend fun update(downloadInfoModel: MapDownloadingInfoModel) {
        return downloadDao.update(downloadInfoModel.toDbEntity())
    }

    override fun updateState(mapId : String, state : DownloadState) {
        downloadDao
            .updateState(mapId, state.toDbEntity().toString())
    }
}