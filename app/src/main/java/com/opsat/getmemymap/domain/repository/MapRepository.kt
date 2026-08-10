package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.MapInfoModel

interface MapRepository {
    fun getMapInfoList(parentMapName : String?) : List<MapInfoModel>

    fun getMapById(mapId : String) : MapInfoModel
}