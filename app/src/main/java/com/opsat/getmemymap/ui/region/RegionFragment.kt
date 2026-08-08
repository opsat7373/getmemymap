package com.opsat.getmemymap.ui.region

import android.os.Bundle
import android.os.StatFs
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.opsat.getmemymap.R
import com.opsat.getmemymap.databinding.FragmentMemoryMonitorBinding
import com.opsat.getmemymap.databinding.RegionItemListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class RegionFragment : Fragment() {

    private lateinit var binding: RegionItemListBinding

    val regionViewModel : RegionViewModel by viewModels()

    val args: RegionFragmentArgs by navArgs()


    val adapter = MyRegionRecyclerViewAdapter({region -> regionViewModel.stopDownloadMap(region)}) { region ->
        if (region.hasChild) {
            findNavController().navigate(RegionFragmentDirections.actionRegionFragmentSelf(region.name))
        } else {
            regionViewModel.startDownloadMap(region)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegionItemListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        val regionName = args.parentRegionName
        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = if (regionName == "europe") "Downloads Map" else regionName.replaceFirstChar { it.uppercase() }
        binding.memoryMonitorContainer.visibility = if(args.showMemoryInfo) View.VISIBLE else View.GONE
        observeRegions()
        regionViewModel.selectParent(args.parentRegionName)

    }

    private fun observeRegions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                regionViewModel.regions.collect { regions ->
                    adapter.setList(regions)
                }
            }
        }
    }
}