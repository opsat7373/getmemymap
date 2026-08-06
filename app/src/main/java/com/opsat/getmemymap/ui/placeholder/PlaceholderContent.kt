package com.opsat.getmemymap.ui.placeholder

import com.opsat.getmemymap.domain.model.RegionModel
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object PlaceholderContent {


    /**
     * A placeholder item representing a piece of content.
     */
    data class PlaceholderItem(val id: String, val content: RegionModel, val details: String) {
        override fun toString(): String = content.name
    }
}