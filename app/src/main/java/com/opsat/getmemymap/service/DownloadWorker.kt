package com.opsat.getmemymap.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.opsat.getmemymap.R
import com.opsat.getmemymap.data.downloader.DownloadController
import com.opsat.getmemymap.data.downloader.DownloadResult
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.Call
import java.io.File
import java.io.IOException

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: DownloadRepository,
    private val downloadController : DownloadController
) : CoroutineWorker(
    appContext,
    workerParams
) {

    private var currentCall: Call? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                applicationContext.getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    override suspend fun doWork(): Result {
        createNotificationChannel()
        do {
            val downloadInfoModel =
                repository.getEnqueuedDownloads()
            if (downloadInfoModel  != null) {

                setForeground(
                    createForegroundInfo(
                        downloadInfoModel.regionName,
                        0
                    )
                )
                try {
                    repository.updateDownload(
                        downloadInfoModel.copy(
                            state = DownloadState.DOWNLOADING
                        )
                    )
                    val url = "https://download.osmand.net/download.php?standard=yes&file=${downloadInfoModel.downloadUrl}"
                    val directory = applicationContext.filesDir

                    val partFileName = "${downloadInfoModel.localFile}_part"
                    val destinationFileName = "${downloadInfoModel.localFile}"


                    val file = File(directory, partFileName)

                    file.delete()

                    val downloadFlow = downloadController.download(
                        url,
                        downloadInfoModel.regionId,
                        file
                    )

                    downloadFlow.collect { downloadResult ->
                        when(downloadResult) {
                            DownloadResult.Cancelled -> {
                                file.delete()
                            }
                            is DownloadResult.DownloadProgress -> {
                                val progress =
                                    ((downloadResult.downloadedBytes * 100) / downloadResult.totalBytes)
                                        .toInt()

                                repository.updateDownload(
                                    downloadInfoModel.copy(
                                        state = DownloadState.DOWNLOADING,
                                        downloadedBytes = downloadResult.downloadedBytes,
                                        totalBytes = downloadResult.totalBytes
                                    )

                                )

                                setForeground(
                                    createForegroundInfo(
                                        downloadInfoModel.regionName,
                                        progress
                                    )
                                )
                            }

                            is DownloadResult.Error -> repository.updateDownload(
                                downloadInfoModel.copy(
                                    state = DownloadState.FAILED
                                )
                            )
                            DownloadResult.Success -> {

                                file.renameTo(
                                    File(file.parentFile, destinationFileName)
                                )

                                repository.updateDownload(
                                    downloadInfoModel.copy(
                                        state = DownloadState.COMPLETED
                                    )
                                )
                            }
                        }
                    }


                } catch (e: IOException) {
                    if (currentCall?.isCanceled() == true ) {
                        val a = 5
                    } else {

                        repository.updateDownload(
                            downloadInfoModel.copy(
                                state = DownloadState.FAILED
                            )
                        )
                        return Result.retry()
                    }

                } catch (e: Exception) {

                    repository.updateDownload(
                        downloadInfoModel.copy(
                            state = DownloadState.FAILED
                        )
                    )
                    return Result.failure()
                }
            }

        } while (downloadInfoModel != null)
        return Result.success()
    }

    private fun createForegroundInfo(
        fileName: String,
        progress: Int
    ): ForegroundInfo {

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_action_import
                )
                .setContentTitle(
                    "Downloading"
                )
                .setContentText(
                    fileName
                )
                .setProgress(
                    100,
                    progress,
                    false
                )
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build()

        return ForegroundInfo(
            NOTIFICATION_ID,
            notification
        )
    }

    companion object {

        const val DOWNLOAD_ID = "download_id"

        private const val CHANNEL_ID = "downloads"
        private const val NOTIFICATION_ID = 1001
    }
}