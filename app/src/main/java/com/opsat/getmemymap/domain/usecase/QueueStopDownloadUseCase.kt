package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.data.downloader.DownloadController
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.MapRepository
import javax.inject.Inject

class QueueStopDownloadUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val downloadRepository: DownloadRepository,
    private val downloadController : DownloadController
) {

    suspend operator fun invoke(
        mapId: String
    ) {
        val map = mapRepository.getMapById(mapId)
        downloadRepository.stopDownloadMap(map)
        downloadController.cancel(mapId)
    }
}