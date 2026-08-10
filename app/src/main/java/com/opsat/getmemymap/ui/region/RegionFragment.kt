package com.opsat.getmemymap.ui.region

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
import com.opsat.getmemymap.data.network.NetworkChecker
import com.opsat.getmemymap.databinding.RegionItemListBinding
import com.opsat.getmemymap.ui.NetworkPolicy
import com.opsat.getmemymap.ui.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class RegionFragment : Fragment() {

    private lateinit var binding: RegionItemListBinding

    @Inject
    lateinit var networkChecker: NetworkChecker

    val regionViewModel : RegionViewModel by viewModels()

    private val sessionViewModel: SessionViewModel by activityViewModels()

    val args: RegionFragmentArgs by navArgs()

    val adapter = MyRegionRecyclerViewAdapter({region -> regionViewModel.stopDownloadMap(region)}) { regionItem ->
        if (regionItem.hasChild) {
            findNavController().navigate(RegionFragmentDirections.actionRegionFragmentSelf(regionItem.regionName))
        } else {
            when {
                networkChecker.isWifiConnected() -> {
                    regionViewModel.startDownloadMap(regionItem.regionId, allowMobileData = false)
                }

                networkChecker.isMobileDataConnected() -> {
                    handleMobileNetwork(regionItem.regionId)
                }

                else -> {
                    showNoInternetMessage()
                }
            }

        }
    }

    private fun handleMobileNetwork(regionId : String) {

        when (sessionViewModel.networkPolicy) {

            NetworkPolicy.ANY_NETWORK -> {
                regionViewModel.startDownloadMap(regionId, allowMobileData = true)
            }

            NetworkPolicy.WIFI_ONLY -> {
                showWifiOnlyMessage()
            }

            null -> {
                showMobileDataDialog() {
                    regionViewModel.startDownloadMap(regionId, allowMobileData = true)
                }
            }
        }
    }

    private fun showNoInternetMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Немає підключення")
            .setMessage(
                "Для завантаження потрібне підключення до Інтернету."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showWifiOnlyMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Потрібен Wi-Fi")
            .setMessage(
                "Ви заборонили використання мобільних даних. " +
                        "Завантаження продовжиться, коли буде доступне Wi-Fi."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showMobileDataDialog(onPositiveButtonClicked : () -> Unit = {}) {

        AlertDialog.Builder(requireContext())
            .setTitle("Мобільні дані")
            .setMessage(
                "Wi-Fi недоступний. " +
                        "Використати мобільні дані для завантаження?"
            )
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
        binding.europeLabel.visibility = if(args.showMemoryInfo) View.VISIBLE else View.GONE
        observeRegions()
        regionViewModel.selectParent(args.parentRegionName)

    }

    private fun observeRegions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                regionViewModel.regions.collect { regionsList ->
                    adapter.submitList(regionsList)
                }
            }
        }
    }
}