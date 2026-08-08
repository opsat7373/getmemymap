package com.opsat.getmemymap.data.local.database

enum class DbDownloadState {
    QUEUED,
    DOWNLOADING,
    UNKNOWN,
    COMPLETED,
    FAILED
}