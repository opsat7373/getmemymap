package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.DownloadScheduler
import javax.inject.Inject

class QueueDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val scheduler: DownloadScheduler
) {

    suspend operator fun invoke(
        region: RegionModel
    ) {

        downloadRepository.addDownloadMap(region)

        val downloadFileName = region.downloadFileName
        if (downloadFileName != null) {
            scheduler.schedule(downloadFileName)
        }
    }
}