package com.opsat.getmemymap.domain.repository

interface DownloadScheduler {

    fun schedule(downloadId: String)
}