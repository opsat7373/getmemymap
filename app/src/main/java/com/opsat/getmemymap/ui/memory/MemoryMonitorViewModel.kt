package com.opsat.getmemymap.ui.memory

import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MemoryMonitorViewModel : ViewModel() {
    private val _storage = MutableStateFlow<StorageInfo?>(null)
    val storage = _storage.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                updateStorage()
                delay(10000)
            }
        }
    }

    private fun updateStorage() {
        val stat = StatFs(Environment.getDataDirectory().path)

        val freeBytes = stat.availableBytes
        val totalBytes = stat.totalBytes

        _storage.value = StorageInfo(
            freeBytes = freeBytes,
            totalBytes = totalBytes
        )
    }
}

data class StorageInfo(
    val freeBytes: Long,
    val totalBytes: Long
)