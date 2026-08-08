package com.opsat.getmemymap.ui.region

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import com.opsat.getmemymap.domain.repository.RegionRepository
import com.opsat.getmemymap.domain.usecase.GetRegionsWithDownloadStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val getRegionListUseCase : GetRegionsWithDownloadStateUseCase,
    private val downloadRepository: DownloadRepository
): ViewModel() {

    private val selectedParentName = MutableStateFlow("europe")

    val regions: StateFlow<List<RegionModel>> =
        selectedParentName
            .flatMapLatest { parentRegion ->
                getRegionListUseCase(parentRegion)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList())

    fun selectParent(parentName: String) {
        selectedParentName.value = parentName
    }

    fun startDownloadMap(region : RegionModel) {
        viewModelScope.launch {
            downloadRepository.addDownloadMap(region)
        }
    }

    fun stopDownloadMap(region : RegionModel) {
        viewModelScope.launch {
            downloadRepository.stopDownloadMap(region)
        }
    }
}