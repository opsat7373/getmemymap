package com.opsat.getmemymap.data.local.database

enum class DbDownloadState {
    QUEUED,
    DOWNLOADING,
    SUSPENDED,
    COMPLETED,
    FAILED
}