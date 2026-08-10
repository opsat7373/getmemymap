package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.data.local.xml.AssetsDataSource
import com.opsat.getmemymap.data.mapper.toDomainModel
import com.opsat.getmemymap.domain.model.RegionInfoModel
import com.opsat.getmemymap.domain.repository.RegionRepository
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    assetsDataSource: AssetsDataSource
) : RegionRepository {

    var regionMap : Map<String?, List<RegionInfoModel>>
    init {
        val xmlRegionMap = assetsDataSource.getMapsList()
        regionMap = xmlRegionMap.mapValues { xmlRegionEntry ->
            xmlRegionEntry.value.map { xmlRegion -> xmlRegion.toDomainModel() }
        }
    }


    override fun getRegionsInfoList(parentRegionName: String?): List<RegionInfoModel>{
        return regionMap[parentRegionName] ?: emptyList()
    }

    override fun getRegionById(regionId: String): RegionInfoModel {
        return regionMap
            .flatMap { regionMapEntry -> regionMapEntry.value  }
            .first { region ->
                region.regionId == regionId

            }
    }
}