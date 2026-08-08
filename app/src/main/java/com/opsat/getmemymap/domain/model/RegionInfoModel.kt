package com.opsat.getmemymap.domain.model

class RegionInfoModel (val name : String,
                      val parentRegionName: String,
                      val downloadFileName: String? = null,
                      val hasChild: Boolean)