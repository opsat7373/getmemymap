package com.opsat.getmemymap.data.downloader

sealed interface DownloadResult {

    data object Success : DownloadResult

    data object Cancelled : DownloadResult

    data class DownloadProgress  (
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : DownloadResult {
        val progress: Int
            get() = if (totalBytes > 0) {
                ((downloadedBytes * 100) / totalBytes).toInt()
            } else {
                0
            }
    }

    data class Error(
        val exception: Exception
    ) : DownloadResult
}