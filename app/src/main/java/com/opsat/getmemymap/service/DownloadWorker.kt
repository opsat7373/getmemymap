package com.opsat.getmemymap.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.opsat.getmemymap.R
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionDownloadInfoModel
import com.opsat.getmemymap.domain.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: DownloadRepository,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(
    appContext,
    workerParams
) {

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
        val downloadId =
            inputData.getString(DOWNLOAD_ID)
                ?: return Result.failure()

        val downloadModelList =
            repository.getEnqueuedDownloads().first()
        downloadModelList.forEach { downloadInfoModel ->

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

                downloadFile(downloadInfoModel)

                repository.updateDownload(
                    downloadInfoModel.copy(
                        state = DownloadState.COMPLETED
                    )
                )

            } catch (e: IOException) {

                repository.updateDownload(
                    downloadInfoModel.copy(
                        state = DownloadState.FAILED
                    )
                )

                return Result.retry()

            } catch (e: Exception) {

                repository.updateDownload(
                    downloadInfoModel.copy(
                        state = DownloadState.FAILED
                    )
                )

                return Result.failure()
            }

        }
        return Result.success()
    }

    private suspend fun downloadFile(
        downloadModel: RegionDownloadInfoModel
    ) = withContext(Dispatchers.IO) {

        val request = Request.Builder()
            .url("https://download.osmand.net/download.php?standard=yes&file=${downloadModel.downloadUrl}")
            .build()

        okHttpClient
            .newCall(request)
            .execute()
            .use { response ->

                if (!response.isSuccessful) {
                    throw IOException(
                        "HTTP ${response.code}"
                    )
                }

                val body = response.body
                    ?: throw IOException("Empty response body")

                val totalBytes = body.contentLength()

                if (totalBytes <= 0) {
                    throw IOException(
                        "Unknown content length"
                    )
                }
                val directory = applicationContext.filesDir

                val file = File(directory,downloadModel.localFile)

                file.parentFile?.mkdirs()

                body.byteStream().use { input ->

                    FileOutputStream(file).use { output ->

                        val buffer = ByteArray(8 * 1024)

                        var downloadedBytes = 0L
                        var lastProgress = -1

                        while (true) {

                            val read = input.read(buffer)

                            if (read == -1) {
                                break
                            }

                            output.write(
                                buffer,
                                0,
                                read
                            )

                            downloadedBytes += read

                            val progress =
                                ((downloadedBytes * 100) / totalBytes)
                                    .toInt()

                            if (progress != lastProgress) {

                                lastProgress = progress

                                repository.updateDownload(
                                    downloadModel.copy(
                                        downloadedBytes = downloadedBytes,
                                        totalBytes = totalBytes
                                    )

                                )

                                setForeground(
                                    createForegroundInfo(
                                        downloadModel.regionName,
                                        progress
                                    )
                                )


                            }
                        }

                        output.flush()
                    }
                }
            }
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