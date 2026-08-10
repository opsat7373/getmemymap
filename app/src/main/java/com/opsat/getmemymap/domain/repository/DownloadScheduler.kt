package com.opsat.getmemymap.domain.repository

interface DownloadScheduler {

    fun schedule(allowMobileData: Boolean)
}