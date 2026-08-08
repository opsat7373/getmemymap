package com.opsat.getmemymap.domain.usecase

import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.RegionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRegionsWithDownloadStateUseCase @Inject constructor(
    private val regionRepository: RegionRepository,
    private val downloadRepository: DownloadRepository
) {

    operator fun invoke(parentRegion : String?): Flow<List<RegionModel>> {
        val regionsList =  regionRepository.getRegionsInfoList(parentRegion)
        return downloadRepository
            .getRegionDownloadInfo(parentRegion)
            .map { downloadsList ->
                regionsList.map { region ->
                    val downloadInfo = downloadsList.firstOrNull { info ->
                        info.regionName == region.name
                    }
                    RegionModel(
                        region.name,
                        region.parentRegionName,
                        region.downloadFileName,
                        hasChild = region.hasChild,
                        downloadInfo?.state ?: DownloadState.UNKNOWN,
                        downloadedBytes = downloadInfo?.downloadedBytes ?: 0L,
                        totalBytes = downloadInfo?.totalBytes ?: 0L
                    )
                }
        }
    }
}