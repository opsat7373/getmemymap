package com.opsat.getmemymap.domain.model

class MapInfoModel (
    val regionId : String,
    val name : String,
    val translate : String,
    val parentRegionName : String,
    val downloadFileName : String? = null,
    val hasChild : Boolean,
    val downloadAvailable : Boolean)