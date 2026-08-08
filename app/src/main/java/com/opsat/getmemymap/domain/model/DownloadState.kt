package com.opsat.getmemymap.domain.model

enum class DownloadState {
    QUEUED,
    DOWNLOADING,
    UNKNOWN,
    COMPLETED,
    FAILED
}