package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.domain.model.MapModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMapsWithDownloadStateUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val downloadRepository: DownloadRepository
) {

    operator fun invoke(parentMapId : String?): Flow<List<MapModel>> {
        val mapsList =  mapRepository.getMapInfoList(parentMapId)
        return downloadRepository
            .getMapDownloadInfo(parentMapId)
            .map { downloadsList ->
                mapsList.map { map ->
                    val downloadInfo = downloadsList.firstOrNull { info ->
                        info.mapName == map.name
                    }
                    MapModel(
                        mapId = map.mapId,
                        map.name,
                        translate = map.translate,
                        map.parentMapId,
                        map.downloadFileName,
                        hasChild = map.hasChild,
                        downloadAvailable = map.downloadAvailable,
                        downloadInfo = downloadInfo
                    )
                }
        }
    }
}