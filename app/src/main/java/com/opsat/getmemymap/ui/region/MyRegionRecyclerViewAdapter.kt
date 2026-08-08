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
import com.opsat.getmemymap.R
import com.opsat.getmemymap.databinding.RegionItemBinding
import com.opsat.getmemymap.domain.model.DownloadState
import com.opsat.getmemymap.domain.model.RegionModel

class MyRegionRecyclerViewAdapter(val onCancelClick : (RegionModel) -> Unit = {}, val onItemClick : (RegionModel) -> Unit = {} ) : RecyclerView.Adapter<MyRegionRecyclerViewAdapter.ViewHolder>() {

    private val values = mutableListOf<RegionModel>()

    fun setList(list : List<RegionModel>) {
        values.clear()
        values += list
        notifyDataSetChanged()
    }

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
        val item = values[position]
        val downloadStarted = item.state == DownloadState.DOWNLOADING || item.state == DownloadState.QUEUED
        holder.contentView.text = item.name
        holder.downloadButton.visibility = if (!item.hasChild && item.state == DownloadState.UNKNOWN) View.VISIBLE else View.INVISIBLE
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

        val progress = ((item.downloadedBytes.toDouble() / maxOf(item.totalBytes, 1)) * 100).toInt()
        holder.progressBar.setProgress(progress, true)
        holder.root.setOnClickListener {
            onItemClick(item)
        }
        holder.cancelButton.setOnClickListener {
            onCancelClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

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

}