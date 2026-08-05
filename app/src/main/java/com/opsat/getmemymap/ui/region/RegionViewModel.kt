package com.opsat.getmemymap.ui.region

import androidx.lifecycle.ViewModel
import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.RegionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val repository: RegionRepository
): ViewModel() {


    fun getRegionsList() : List<RegionModel> {
        return repository.getRegionsList()
    }
}