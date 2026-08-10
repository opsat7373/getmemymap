package com.opsat.getmemymap.ui.region

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.getmemymap.domain.usecase.GetRegionsWithDownloadStateUseCase
import com.opsat.getmemymap.domain.usecase.QueueDownloadUseCase
import com.opsat.getmemymap.domain.usecase.QueueStopDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val getRegionListUseCase : GetRegionsWithDownloadStateUseCase,
    private val queueDownloadUseCase: QueueDownloadUseCase,
    private val queueStopDownloadUseCase: QueueStopDownloadUseCase,
): ViewModel() {

    private val selectedParentName = MutableStateFlow("europe")

    val regions: StateFlow<List<RegionUIItem>> =
        selectedParentName
            .flatMapLatest { parentRegion ->
                getRegionListUseCase(parentRegion).map { regionModelList ->
                    regionModelList.map { regionModel ->
                        val downloadInfo = regionModel.downloadInfo
                        val downloadProgress = if (downloadInfo != null) {
                                ((downloadInfo.downloadedBytes.toDouble() / maxOf(
                                    downloadInfo.totalBytes,
                                    1
                                )) * 100).toInt()
                        } else 0
                        RegionUIItem(
                            regionId = regionModel.regionId,
                            regionName = regionModel.name,
                            regionTranslatedName = regionModel.translate,
                            parentRegionName = regionModel.parentRegionName,
                            hasChild = regionModel.hasChild,
                            canDownload = !regionModel.hasChild && downloadInfo?.state == null,
                            downloadProgress = downloadProgress,
                            state = regionModel.downloadInfo?.state,
                            downloadFileName = regionModel.downloadFileName
                        )

                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList())

    fun selectParent(parentName: String) {
        selectedParentName.value = parentName
    }

    fun startDownloadMap(regionId : String, allowMobileData : Boolean) {
        viewModelScope.launch {
            queueDownloadUseCase(regionId, allowMobileData)
        }
    }

    fun stopDownloadMap(region : RegionUIItem) {
        viewModelScope.launch {
            queueStopDownloadUseCase(region.regionId)
        }
    }
}