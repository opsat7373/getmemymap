package com.opsat.getmemymap.service

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.opsat.getmemymap.R
import com.opsat.getmemymap.data.local.database.DownloadEntity
import com.opsat.getmemymap.domain.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
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

    override suspend fun doWork(): Result {
        val a = 6

//        val downloadId =
//            inputData.getString(DOWNLOAD_ID)
//                ?: return Result.failure()
//
//        val download =
//            repository.getDownload(downloadId)
//                ?: return Result.failure()
//
//        setForeground(
//            createForegroundInfo(
//                download.fileName,
//                0
//            )
//        )
//
//        return try {
//
//            repository.updateStatus(
//                downloadId,
//                DownloadStatus.DOWNLOADING
//            )
//
//            downloadFile(download)
//
//            repository.updateStatus(
//                downloadId,
//                DownloadStatus.COMPLETED
//            )
//
//            Result.success()
//
//        } catch (e: IOException) {
//
//            repository.updateStatus(
//                downloadId,
//                DownloadStatus.FAILED
//            )
//
//            Result.retry()
//
//        } catch (e: Exception) {
//
//            repository.updateStatus(
//                downloadId,
//                DownloadStatus.FAILED
//            )
//
//            Result.failure()
//        }
        return Result.success()
    }

    private suspend fun downloadFile(
        download: DownloadEntity
    ) = withContext(Dispatchers.IO) {

        val request = Request.Builder()
            .url(download.downloadUrl)
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

                val file = File(download.localFile)

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

                                /*repository.updateProgress(
                                    id = download.id,
                                    progress = progress,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes
                                )

                                setForeground(
                                    createForegroundInfo(
                                        download.fileName,
                                        progress
                                    )
                                )

                                 */
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