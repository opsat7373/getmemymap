package com.opsat.getmemymap.data.downloader

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DownloadController @Inject constructor(
    private val client: OkHttpClient
) {
    private var currentCall: Call? = null

    private var currentMapId: String? = null

    fun download(
        url: String,
        mapId: String,
        outputFile: File,
        downloadedBytes : Long = 0,
    ): Flow<DownloadResult> = callbackFlow {
        val request = Request.Builder()
            .url(url)
            .header("Range", "bytes=$downloadedBytes-")
            .build()

        val call = client.newCall(request)

        currentCall = call
        currentMapId = mapId

        call.enqueue(
            object : Callback {
                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {
                    if (call.isCanceled()) {
                        trySend(DownloadResult.Cancelled)
                        close()
                    } else {
                        Timber.e(e)
                        trySend(
                            DownloadResult.Error(e)
                        )
                        close()
                    }
                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {
                    val body = response.body

                    val totalBytes = getTotalBytes(response)

                    if (totalBytes <= 0) {
                        throw IOException(
                            "Unknown content length"
                        )
                    }

                    outputFile.parentFile?.mkdirs()

                    val append = downloadedBytes != 0L

                    try {

                        body.byteStream().use { input ->

                            FileOutputStream(outputFile, append).use { output ->

                                val buffer = ByteArray(8 * 1024)

                                var downloadedBytes = downloadedBytes
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

                                        val progress =
                                            DownloadResult.DownloadProgress(
                                                downloadedBytes,
                                                totalBytes
                                            )
                                        trySend(progress)
                                    }
                                }

                                output.flush()
                            }
                        }
                        trySend(DownloadResult.Success)
                    } catch (exception : IOException) {
                        if (call.isCanceled()) {
                            trySend(DownloadResult.Cancelled)
                            close()
                        } else {
                            trySend(
                                DownloadResult.Error(exception)
                            )
                            close()
                        }
                    }
                    close()
                }
            }
        )
        awaitClose {
            currentCall = null
            currentMapId = null
        }
    }

    private fun getTotalBytes(response: Response): Long {
        val contentRange = response.header("Content-Range")

        if (contentRange != null) {
            return contentRange.substringAfter("/")
                .toLong()
        }

        return response.body.contentLength()
    }

    fun cancel(mapId : String) {
        if (currentMapId == mapId) {
            currentCall?.cancel()
            currentMapId = null
        }
    }
}