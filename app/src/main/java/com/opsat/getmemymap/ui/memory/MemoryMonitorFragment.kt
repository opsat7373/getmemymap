package com.opsat.getmemymap.ui.memory

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.opsat.getmemymap.databinding.FragmentMemoryMonitorBinding
import kotlinx.coroutines.launch
import java.util.Locale

class MemoryMonitorFragment : Fragment() {

    private lateinit var binding: FragmentMemoryMonitorBinding
    private val viewModel: MemoryMonitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemoryMonitorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMemoryInfo()
    }

    private fun observeMemoryInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.storage.collect { memoryInfo ->
                    val total = memoryInfo?.totalBytes ?: 1
                    val free = memoryInfo?.freeBytes ?: 0
                    val freeGb = free  / 1024.0 / 1024.0 / 1024.0
                    val percent = (free / total.toDouble()) * 100
                    binding.deviceMemoryInfo.text = String.format(
                        Locale.US,
                        "Free %.2f Gb",
                        freeGb
                    )
                    binding.memoryInfoProgress.progress = percent.toInt()
                }
            }
        }
    }
}