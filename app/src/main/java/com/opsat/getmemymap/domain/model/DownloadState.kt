package com.opsat.getmemymap.domain.model

enum class DownloadState {
    QUEUED,
    DOWNLOADING,
    SUSPENDED,
    COMPLETED,
    FAILED
}