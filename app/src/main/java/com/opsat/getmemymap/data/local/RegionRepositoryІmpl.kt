package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.RegionRepository
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    private val assetsDataSource: AssetsDataSource) : RegionRepository {

        var regionMap : Map<String?, List<RegionModel>> = assetsDataSource.getMapsList()


    override fun getRegionsList(parentRegionName: String?): List<RegionModel>{
        return regionMap[parentRegionName] ?: emptyList()
    }
}