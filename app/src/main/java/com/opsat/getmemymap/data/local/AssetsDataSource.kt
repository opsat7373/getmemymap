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

    fun getMapsList() : List<RegionModel> {

        val parser = Xml.newPullParser()
        parser.setInput(context.assets.open("regions.xml"), "UTF-8")

        val stack = ArrayDeque<RegionModel>()
        val roots = mutableListOf<RegionModel>()

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {

            when (parser.eventType) {

                XmlPullParser.START_TAG -> {

                    if (parser.name == "region") {

                        val name = parser.getAttributeValue(null, "name") ?: ""

                        val region = RegionModel(name)

                        if (stack.isEmpty()) {
                            roots += region
                        } else {
                            stack.last().children += region
                        }

                        stack.addLast(region)
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

        return roots.first().children
    }
}