package com.opsat.getmemymap.data.local

import com.opsat.getmemymap.domain.model.RegionModel
import com.opsat.getmemymap.domain.repository.RegionRepository

class RegionRepositoryІmpl : RegionRepository {
    override suspend fun getRegionsList(): List<RegionModel>{
        return listOf(
            RegionModel("Kyiv"),
            RegionModel("Sumy"),
            RegionModel("Dnipro"),
            RegionModel("Odessa"),
        )
    }
}