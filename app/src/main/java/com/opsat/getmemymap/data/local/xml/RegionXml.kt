package com.opsat.getmemymap.data.local.xml

data class RegionXml (val name : String,
                      val parentRegionName : String?,
                      val downloadPrefix : String? = null,
                      var hasChild: Boolean
)