package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.data.local.xml.AssetsDataSource
import com.opsat.getmemymap.data.mapper.toDomainModel
import com.opsat.getmemymap.domain.model.MapInfoModel
import com.opsat.getmemymap.domain.repository.MapRepository
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    assetsDataSource: AssetsDataSource
) : MapRepository {

    var map : Map<String?, List<MapInfoModel>>
    init {
        val xmlMap = assetsDataSource.getMapsList()
        map = xmlMap.mapValues { xmlMapEntry ->
            xmlMapEntry.value.map { xmlMapValue -> xmlMapValue.toDomainModel() }
        }
    }


    override fun getMapInfoList(parentMapName: String?): List<MapInfoModel>{
        return map[parentMapName] ?: emptyList()
    }

    override fun getMapById(mapId: String): MapInfoModel {
        return map
            .flatMap { mapEntry -> mapEntry.value  }
            .first { map ->
                map.mapId == mapId

            }
    }
}