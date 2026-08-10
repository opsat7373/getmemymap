package com.opsat.getmemymap.domain.model

class MapInfoModel (
    val mapId : String,
    val name : String,
    val translate : String,
    val parentMapId : String,
    val downloadFileName : String? = null,
    val hasChild : Boolean,
    val downloadAvailable : Boolean)