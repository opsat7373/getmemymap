package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.RegionInfoModel

interface RegionRepository {
    fun getRegionsInfoList(parentRegionName : String?) : List<RegionInfoModel>
}