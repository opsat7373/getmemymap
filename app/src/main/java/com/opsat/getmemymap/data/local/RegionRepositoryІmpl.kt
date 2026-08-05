package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.RegionRepository
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    private val assetsDataSource: AssetsDataSource) : RegionRepository {



    override fun getRegionsList(): List<RegionModel>{
        return assetsDataSource.getMapsList()
    }
}