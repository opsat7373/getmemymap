package com.opsat.getmemymap.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsat.getmemymap.R
import com.opsat.getmemymap.data.network.NetworkChecker
import com.opsat.getmemymap.databinding.MapItemListBinding
import com.opsat.getmemymap.ui.NetworkPolicy
import com.opsat.getmemymap.ui.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class MapListFragment : Fragment() {

    private lateinit var binding: MapItemListBinding

    @Inject
    lateinit var networkChecker: NetworkChecker

    val mapListViewModel : MapListViewModel by viewModels()

    private val sessionViewModel: SessionViewModel by activityViewModels()

    val args: MapListFragmentArgs by navArgs()

    private val adapter by lazy {
        MapListRecyclerViewAdapter(
            onCancelClick = mapListViewModel::stopDownloadMap,
            onDownloadClick = ::handleDownload,
            onItemClick = ::onMapItemClicked
        )
    }

    private fun onMapItemClicked(map: MapUIItem) {
        if (map.hasChild) {
            findNavController().navigate(
                MapListFragmentDirections
                    .actionMapFragmentSelf(map.mapName)
            )
            return
        }
    }

    private fun handleDownload(map: MapUIItem) {
        when {
            networkChecker.isWifiConnected() -> {
                mapListViewModel.startDownloadMap(
                    mapId = map.mapId,
                    allowMobileData = false
                )
            }

            networkChecker.isMobileDataConnected() -> {
                handleMobileNetwork(map.mapId)
            }

            else -> {
                showNoInternetMessage()
            }
        }
    }

    private fun handleMobileNetwork(mapId : String) {

        when (sessionViewModel.networkPolicy) {

            NetworkPolicy.ANY_NETWORK -> {
                mapListViewModel.startDownloadMap(mapId, allowMobileData = true)
            }

            NetworkPolicy.WIFI_ONLY -> {
                showWifiOnlyMessage()
            }

            null -> {
                showMobileDataDialog {
                    mapListViewModel.startDownloadMap(mapId, allowMobileData = true)
                }
            }
        }
    }

    private fun showNoInternetMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.not_connection_dialog_title))
            .setMessage(
                getString(R.string.no_connection_dialog_message)
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showWifiOnlyMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.wi_fi_dialog_title))
            .setMessage(getString(R.string.wwi_fi_dialog_message))
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showMobileDataDialog(onPositiveButtonClicked : () -> Unit = {}) {

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.cell_data_dialog_title))
            .setMessage(getString(R.string.cell_data_dialog_message))
            .setNegativeButton("Скасувати", null)
            .setPositiveButton("Завантажити") { _, _ ->
                sessionViewModel.setNetworkPolicy(NetworkPolicy.ANY_NETWORK)
                onPositiveButtonClicked()
            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapItemListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        val mapName = args.parentMapName
        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = if (mapName == "europe") getString(R.string.downloads_map_title) else mapName.replaceFirstChar { it.uppercase() }
        binding.memoryMonitorContainer.visibility = if(args.showMemoryInfo) View.VISIBLE else View.GONE
        binding.europeLabel.visibility = if(args.showMemoryInfo) View.VISIBLE else View.GONE
        observeMaps()
        mapListViewModel.selectParent(args.parentMapName)

    }

    private fun observeMaps() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                mapListViewModel.maps.collect { mapsList ->
                    adapter.submitList(mapsList)
                }
            }
        }
    }
}