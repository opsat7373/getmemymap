package com.opsat.getmemymap.ui.map

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.opsat.getmemymap.R
import com.opsat.getmemymap.databinding.MapItemBinding
import com.opsat.getmemymap.domain.model.DownloadState

class MapListRecyclerViewAdapter(private val onCancelClick : (MapUIItem) -> Unit,
                                 private val onDownloadClick : (MapUIItem) -> Unit,
                                 private val onItemClick : (MapUIItem) -> Unit )
    : ListAdapter<MapUIItem, MapListRecyclerViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            MapItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: MapItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MapUIItem) {
            val context = binding.root.context

            binding.content.text = item.mapTranslatedName

            binding.downloadIcon.visibility =
                if (item.canDownload) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

            binding.stopDownloadIcon.visibility =
                if (item.state?.isActive == true) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

            binding.progressBar.visibility =
                if (item.state?.isActive == true) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            val iconColor = if (
                item.state == DownloadState.COMPLETED
            ) {
                R.color.green
            } else {
                R.color.original_icon_color
            }

            binding.mapIcon.imageTintList =
                ContextCompat.getColorStateList(
                    context,
                    iconColor
                )

            binding.progressBar.setProgress(
                item.downloadProgress,
                false
            )

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.downloadIcon.setOnClickListener {
                onDownloadClick(item)
            }

            binding.stopDownloadIcon.setOnClickListener {
                onCancelClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MapUIItem>() {

        override fun areItemsTheSame(
            oldItem: MapUIItem,
            newItem: MapUIItem
        ): Boolean {
            return oldItem.mapId == newItem.mapId
        }

        override fun areContentsTheSame(
            oldItem: MapUIItem,
            newItem: MapUIItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}