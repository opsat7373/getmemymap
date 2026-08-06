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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.opsat.getmemymap.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class RegionFragment : Fragment() {

    val regionViewModel : RegionViewModel by viewModels()

    val args: RegionFragmentArgs by navArgs()


    val adapter = MyRegionRecyclerViewAdapter { region ->
        findNavController().navigate(RegionFragmentDirections.actionRegionFragmentSelf(region.name))
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
        adapter.setList(regionViewModel.getRegionsList(args.parentRegionName))
        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = args.parentRegionName?.replaceFirstChar { it.uppercase() }?: "Downloads Map"

    }
}