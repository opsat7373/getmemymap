package com.opsat.getmemymap.data.local.xml

import android.content.Context
import android.util.Xml
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class AssetsDataSource @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun getMapsList() : Map<String?, List<RegionXml>> {

        val parser = Xml.newPullParser()
        parser.setInput(context.assets.open("regions.xml"), "UTF-8")

        val stack = ArrayDeque<String?>()
        val tempList = mutableMapOf<String?, MutableList<String>>()
        val regionMap = mutableMapOf<String, Map<String, String>>()


        while (parser.eventType != XmlPullParser.END_DOCUMENT) {

            when (parser.eventType) {

                XmlPullParser.START_TAG -> {

                    if (parser.name == "region") {

                        val regionName = parser.getAttributeValue(null, "name") ?: ""
                        val parsedPrefix = parser.getAttributeValue(null, "inner_download_prefix")
                        val innerDownloadPrefix = if (parsedPrefix?.isNotBlank() == true) {
                                "${if (parsedPrefix == $$"$name") regionName else parsedPrefix}_"
                        } else ""


                        val parsedRegionInfo = mapOf("name" to regionName, "inner_download_prefix" to innerDownloadPrefix)
                        regionMap[regionName] = parsedRegionInfo

                        val parentId = if (stack.isEmpty()) {
                            null
                        } else {
                             stack.last()
                        }
                        stack.addLast(regionName)

                        if (!tempList.containsKey(parentId)) {
                            tempList[parentId] = mutableListOf()
                        }
                        tempList[parentId]?.add(regionName)

                    }
                }

                XmlPullParser.END_TAG -> {

                    if (parser.name == "region") {
                        stack.removeLast()
                    }
                }
            }

            parser.next()
        }



        return tempList.mapValues{(parentRegionName, childList) ->
            childList.map { regionName ->
                val regionName = (regionMap[regionName]?: emptyMap()) ["name"] ?: regionName
                val regionPrefix = (regionMap[parentRegionName]?: emptyMap()) ["inner_download_prefix"]
                val region = RegionXml(
                    name = regionName,
                    parentRegionName = parentRegionName,
                    downloadPrefix = regionPrefix,
                    hasChild = tempList[regionName]?.isNotEmpty() ?: false
                    )
                region
            }
        }
    }
}