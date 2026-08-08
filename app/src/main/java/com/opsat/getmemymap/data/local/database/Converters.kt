package com.opsat.getmemymap.data.local.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromDownloadState(state: DbDownloadState): String =
        state.name

    @TypeConverter
    fun toDownloadState(value: String): DbDownloadState =
        DbDownloadState.valueOf(value)
}