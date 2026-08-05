package com.opsat.getmemymap.domain.repository

import com.opsat.getmemymap.domain.model.RegionModel

interface RegionRepository {
    suspend fun getRegionsList() : List<RegionModel>
}