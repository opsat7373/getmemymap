package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.DownloadScheduler
import com.opsat.getmemymap.domain.repository.RegionRepository
import javax.inject.Inject

class QueueStopDownloadUseCase @Inject constructor(
    private val regionRepository: RegionRepository,
    private val downloadRepository: DownloadRepository,
) {

    suspend operator fun invoke(
        regionId: String
    ) {
        val region = regionRepository.getRegionById(regionId)
        downloadRepository.stopDownloadMap(region)
    }
}