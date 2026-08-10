package com.opsat.getmemymap.data.local.xml

data class MapXml (val name : String,
                   val translate : String,
                   val parentMapName : String,
                   val downloadPrefix : String? = null,
                   var hasChild: Boolean,
                   val downloadAvailable : Boolean
)