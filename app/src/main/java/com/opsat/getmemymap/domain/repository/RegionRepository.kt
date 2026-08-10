package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.MapInfoModel

interface RegionRepository {
    fun getRegionsInfoList(parentRegionName : String?) : List<MapInfoModel>

    fun getRegionById(regionId : String) : MapInfoModel
}