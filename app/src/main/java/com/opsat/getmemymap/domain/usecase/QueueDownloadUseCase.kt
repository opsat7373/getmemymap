package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.DownloadScheduler
import com.opsat.getmemymap.domain.repository.MapRepository
import javax.inject.Inject

class QueueDownloadUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val downloadRepository: DownloadRepository,
    private val scheduler: DownloadScheduler
) {

    suspend operator fun invoke(
        mapId: String,
        allowMobileData: Boolean
    ) {
        val map = mapRepository.getMapById(mapId)
        downloadRepository.addDownloadMap(map)
        scheduler.schedule(allowMobileData)
    }
}