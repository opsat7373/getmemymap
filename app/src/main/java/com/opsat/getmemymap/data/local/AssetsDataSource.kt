package com.opsat.getmemymap.data.local

import android.content.Context
import android.util.Xml
import com.opsat.getmemymap.domain.model.RegionModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class AssetsDataSource @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun getMapsList() : Map<String?, MutableList<RegionModel>> {

        val parser = Xml.newPullParser()
        parser.setInput(context.assets.open("regions.xml"), "UTF-8")

        val stack = ArrayDeque<String?>()
        val resultList = mutableMapOf<String?, MutableList<RegionModel>>()

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {

            when (parser.eventType) {

                XmlPullParser.START_TAG -> {

                    if (parser.name == "region") {

                        val name = parser.getAttributeValue(null, "name") ?: ""

                        val region = RegionModel(name)

                        val parentId = if (stack.isEmpty()) {
                            null
                        } else {
                             stack.last()
                        }
                        stack.addLast(name)

                        if (!resultList.containsKey(parentId)) {
                            resultList[parentId] = mutableListOf()
                        }
                        resultList[parentId]?.add(region)

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

        return resultList
    }
}