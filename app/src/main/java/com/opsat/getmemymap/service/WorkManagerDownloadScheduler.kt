package com.opsat.getmemymap.service

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.opsat.getmemymap.domain.repository.DownloadScheduler
import javax.inject.Inject

class WorkManagerDownloadScheduler @Inject constructor(
    private val workManager: WorkManager
) : DownloadScheduler {

    override fun schedule(downloadId: String) {

        val request =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(
                    workDataOf(
                        DownloadWorker.DOWNLOAD_ID to downloadId
                    )
                )
//                .setConstraints(
//                    Constraints.Builder()
//                        .setRequiredNetworkType(
//                            NetworkType.CONNECTED,
//                        )
//                        .build()
//                )
                .build()

        workManager
            .beginUniqueWork(
                DOWNLOAD_QUEUE,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
            .enqueue()
    }

    companion object {
        private const val DOWNLOAD_QUEUE = "download_queue"
    }
}