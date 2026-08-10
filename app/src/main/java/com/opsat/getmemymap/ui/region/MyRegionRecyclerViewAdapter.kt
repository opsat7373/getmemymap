package com.opsat.getmemymap.ui.region

import android.graphics.PorterDuff
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.opsat.getmemymap.R
import com.opsat.getmemymap.databinding.RegionItemBinding
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionModel

class MyRegionRecyclerViewAdapter(val onCancelClick : (RegionUIItem) -> Unit = {},
                                  val onItemClick : (RegionUIItem) -> Unit = {} )
    : ListAdapter<RegionUIItem, MyRegionRecyclerViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RegionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val downloadStarted = item.state == DownloadState.DOWNLOADING || item.state == DownloadState.QUEUED || item.state == DownloadState.SUSPENDED
        holder.contentView.text = item.regionTranslatedName
        holder.downloadButton.visibility = if (item.canDownload) View.VISIBLE else View.INVISIBLE
        holder.cancelButton.visibility = if (downloadStarted) View.VISIBLE else View.INVISIBLE
        holder.progressBar.visibility = if (downloadStarted) View.VISIBLE else View.GONE

        holder.mapIcon.setColorFilter(
            if (item.state == DownloadState.COMPLETED) {
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            } else {
                ContextCompat.getColor(holder.itemView.context, R.color.original_icon_color)
            },
            PorterDuff.Mode.SRC_IN
        )


        holder.progressBar.setProgress(item.downloadProgress, false)
        holder.root.setOnClickListener {
            onItemClick(item)
        }
        holder.cancelButton.setOnClickListener {
            onCancelClick(item)
        }
    }

    class ViewHolder(binding: RegionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val contentView: TextView = binding.content
        val downloadButton: AppCompatImageView = binding.downloadIcon
        val cancelButton: AppCompatImageView = binding.stopDownloadIcon

        val mapIcon : AppCompatImageView = binding.mapIcon

        val progressBar : ProgressBar = binding.progressBar

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RegionUIItem>() {

        override fun areItemsTheSame(
            oldItem: RegionUIItem,
            newItem: RegionUIItem
        ): Boolean {
            return oldItem.downloadFileName == newItem.downloadFileName && oldItem.regionName == newItem.regionName
        }

        override fun areContentsTheSame(
            oldItem: RegionUIItem,
            newItem: RegionUIItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}