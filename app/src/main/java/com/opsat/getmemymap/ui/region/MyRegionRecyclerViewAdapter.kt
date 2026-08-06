package com.opsat.getmemymap.ui.region

import android.graphics.drawable.Icon
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.opsat.getmemymap.ui.placeholder.PlaceholderContent.PlaceholderItem
import com.opsat.getmemymap.databinding.RegionItemBinding
import com.opsat.getmemymap.domain.model.RegionModel

class MyRegionRecyclerViewAdapter(val onItemClick : (RegionModel) -> Unit = {} ) : RecyclerView.Adapter<MyRegionRecyclerViewAdapter.ViewHolder>() {

    private val values = mutableListOf<RegionModel>()

    fun setList(list : List<RegionModel>) {
        values.clear()
        values += list
        notifyItemInserted(list.lastIndex)
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
        holder.contentView.text = item.name
        holder.importButton.visibility = if (item.hasChild) View.INVISIBLE else View.VISIBLE
        if (item.hasChild) {
            holder.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(binding: RegionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val contentView: TextView = binding.content
        val importButton: AppCompatImageView = binding.downloadIcon

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}