package com.opsat.getmemymap.ui.region

import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class RegionFragment : Fragment() {

    val regionViewModel : RegionViewModel by viewModels()

    val args: RegionFragmentArgs by navArgs()


    val adapter = MyRegionRecyclerViewAdapter { region ->
        if (region.hasChild) {
            findNavController().navigate(RegionFragmentDirections.actionRegionFragmentSelf(region.name))
        } else {
            regionViewModel.startDownloadMap(region)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.region_item_list, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.list)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = args.parentRegionName?.replaceFirstChar { it.uppercase() }?: "Downloads Map"
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