package com.opsat.getmemymap.service

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.opsat.getmemymap.domain.repository.DownloadScheduler
import javax.inject.Inject

class WorkManagerDownloadScheduler @Inject constructor(
    private val workManager: WorkManager
) : DownloadScheduler {

    override fun schedule(allowMobileData: Boolean) {

        val request =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(
                            if (allowMobileData) NetworkType.CONNECTED else NetworkType.UNMETERED,
                        )
                        .build()
                )
                .build()

        workManager
            .beginUniqueWork(
                DOWNLOAD_QUEUE,
                ExistingWorkPolicy.REPLACE,
                request
            )
            .enqueue()
    }

    companion object {
        private const val DOWNLOAD_QUEUE = "download_queue"
    }
}