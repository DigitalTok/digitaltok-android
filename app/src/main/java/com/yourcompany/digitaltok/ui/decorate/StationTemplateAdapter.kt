package com.yourcompany.digitaltok.ui.decorate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.digitaltok.R

class StationTemplateAdapter(
    private var items: List<StationTemplateItem>,
    private val onClick: (StationTemplateItem) -> Unit
) : RecyclerView.Adapter<StationTemplateAdapter.VH>() {

    fun submitList(newItems: List<StationTemplateItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station_template, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.badge.setImageResource(item.badgeRes)
        holder.name.text = item.stationName
        holder.line.text = item.lineText
        holder.itemView.setOnClickListener { onClick(item) }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val badge: ImageView = v.findViewById(R.id.ivBadge)
        val name: TextView = v.findViewById(R.id.tvStationName)
        val line: TextView = v.findViewById(R.id.tvLine)
    }
}
