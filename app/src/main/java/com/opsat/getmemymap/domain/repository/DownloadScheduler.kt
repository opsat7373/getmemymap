package com.opsat.getmemymap.domain.repository

interface DownloadScheduler {

    fun schedule(regionId: String)
}