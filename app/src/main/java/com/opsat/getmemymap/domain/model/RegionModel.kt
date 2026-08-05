package com.opsat.getmemymap.domain.model

data class RegionModel (val name : String,     val children: MutableList<RegionModel> = mutableListOf())