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

    fun getMapsList() : Map<String?, List<MapXml>> {

        val parser = Xml.newPullParser()
        parser.setInput(context.assets.open("regions.xml"), "UTF-8")

        val stack = ArrayDeque<String?>()
        val tempList = mutableMapOf<String, MutableList<String>>()
        val mapList = mutableMapOf<String, Map<String, String>>()


        while (parser.eventType != XmlPullParser.END_DOCUMENT) {

            when (parser.eventType) {

                XmlPullParser.START_TAG -> {

                    if (parser.name == "region") {

                        val mapName = parser.getAttributeValue(null, "name") ?: ""
                        val parsedPrefix = parser.getAttributeValue(null, "inner_download_prefix")
                        val translate = parser.getAttributeValue(null, "translate")
                        val type = parser.getAttributeValue(null, "type")
                        val innerDownloadPrefix = if (parsedPrefix?.isNotBlank() == true) {
                                "${if (parsedPrefix == $$"$name") mapName else parsedPrefix}_"
                        } else ""


                        val parsedMapInfo = mapOf("name" to mapName,
                            "inner_download_prefix" to innerDownloadPrefix,
                            "translate" to translate,
                            "type" to type)
                        mapList[mapName] = parsedMapInfo

                        val parentId = if (stack.isEmpty()) {
                            ""
                        } else {
                             stack.last() ?: ""
                        }
                        stack.addLast(mapName)

                        if (!tempList.containsKey(parentId)) {
                            tempList[parentId] = mutableListOf()
                        }
                        tempList[parentId]?.add(mapName)

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



        return tempList.mapValues{(parentMapName, childList) ->
            childList.map { mapName ->
                val mapName = (mapList[mapName]?: emptyMap()) ["name"] ?: mapName
                val mapPrefix = (mapList[parentMapName]?: emptyMap()) ["inner_download_prefix"]
                val translate = (mapList[mapName]?: emptyMap()) ["translate"] ?: mapName
                val type = (mapList[mapName]?: emptyMap()) ["type"] ?: ""
                val mapXml = MapXml(
                    name = mapName,
                    translate = (extractTranslation(translate) ?: mapName).replaceFirstChar { it.uppercase() },
                    parentMapName = parentMapName ?: "",
                    downloadPrefix = mapPrefix,
                    hasChild = tempList[mapName]?.isNotEmpty() ?: false,
                    downloadAvailable = type == "map"
                    )
                mapXml
            }
        }
    }

    private fun extractTranslation(translate: String?): String? {
        if (translate.isNullOrBlank()) {
            return null
        }

        val value = translate
            .split(";")
            .firstOrNull { it.startsWith("name:en=") }
            ?.substringAfter("name:en=")
            ?: translate.substringBefore(";")

        return value.takeIf { it.isNotBlank() }
    }
}