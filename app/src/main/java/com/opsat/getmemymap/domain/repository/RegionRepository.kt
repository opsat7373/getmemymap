package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.RegionModel

interface RegionRepository {
    fun getRegionsList(parentRegionName : String?) : List<RegionModel>
}