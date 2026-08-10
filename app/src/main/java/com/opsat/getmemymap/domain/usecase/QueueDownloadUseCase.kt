package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.DownloadScheduler
import com.opsat.getmemymap.domain.repository.RegionRepository
import javax.inject.Inject

class QueueDownloadUseCase @Inject constructor(
    private val regionRepository: RegionRepository,
    private val downloadRepository: DownloadRepository,
    private val scheduler: DownloadScheduler
) {

    suspend operator fun invoke(
        regionId: String,
        allowMobileData: Boolean
    ) {
        val region = regionRepository.getRegionById(regionId)
        downloadRepository.addDownloadMap(region)
        scheduler.schedule(allowMobileData)
    }
}