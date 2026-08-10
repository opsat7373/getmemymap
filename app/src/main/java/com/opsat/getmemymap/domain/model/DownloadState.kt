package com.opsat.getmemymap.domain.model

enum class DownloadState {
    QUEUED,
    DOWNLOADING,
    SUSPENDED,
    COMPLETED,
    FAILED;

    val isActive: Boolean
        get() = when (this) {
            QUEUED,
            DOWNLOADING,
            SUSPENDED -> true

            else -> false
        }
}