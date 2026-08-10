package com.opsat.getmemymap.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.getmemymap.domain.usecase.GetMapsWithDownloadStateUseCase
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
class MapListViewModel @Inject constructor(
    private val getMapsWithDownloadStateUseCase : GetMapsWithDownloadStateUseCase,
    private val queueDownloadUseCase: QueueDownloadUseCase,
    private val queueStopDownloadUseCase: QueueStopDownloadUseCase,
): ViewModel() {

    private val selectedParentName = MutableStateFlow("europe")

    val maps: StateFlow<List<MapUIItem>> =
        selectedParentName
            .flatMapLatest { parentMap ->
                getMapsWithDownloadStateUseCase(parentMap).map { mapsModelList ->
                    mapsModelList.map { mapModel ->
                        val downloadInfo = mapModel.downloadInfo
                        val downloadProgress = if (downloadInfo != null) {
                                ((downloadInfo.downloadedBytes.toDouble() / maxOf(
                                    downloadInfo.totalBytes,
                                    1
                                )) * 100).toInt()
                        } else 0
                        MapUIItem(
                            mapId = mapModel.mapId,
                            mapName = mapModel.name,
                            mapTranslatedName = mapModel.translate,
                            parentMapId = mapModel.parentMapName,
                            hasChild = mapModel.hasChild,
                            canDownload = !mapModel.hasChild && downloadInfo?.state == null,
                            downloadProgress = downloadProgress,
                            state = mapModel.downloadInfo?.state,
                            downloadFileName = mapModel.downloadFileName
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

    fun startDownloadMap(mapId : String, allowMobileData : Boolean) {
        viewModelScope.launch {
            queueDownloadUseCase(mapId, allowMobileData)
        }
    }

    fun stopDownloadMap(mapUiItem : MapUIItem) {
        viewModelScope.launch {
            queueStopDownloadUseCase(mapUiItem.mapId)
        }
    }
}