package com.opsat.getmemymap.ui.region

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.opsat.getmemymap.ui.placeholder.PlaceholderContent.PlaceholderItem
import com.opsat.getmemymap.databinding.RegionItemBinding
import com.opsat.getmemymap.domain.model.RegionModel

class MyRegionRecyclerViewAdapter(val onItemClick : (String) -> Unit = {} ) : RecyclerView.Adapter<MyRegionRecyclerViewAdapter.ViewHolder>() {

    private val values = mutableListOf<PlaceholderItem>()

    fun setList(list : List<RegionModel>) {
        values.clear()
        list.forEach { region ->
            values.add (PlaceholderItem("", region.name, ""))
        }
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
        holder.idView.text = item.id
        holder.contentView.text = item.content
        holder.contentView.setOnClickListener {
            onItemClick(item.content)
        }
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(binding: RegionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}