package com.yourcompany.digitaltok.ui.decorate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.digitaltok.R

class DecorateAdapter(
    private var items: List<DecorateItem>,
    private val onItemClick: (DecorateItem) -> Unit
) : RecyclerView.Adapter<DecorateAdapter.DecorateViewHolder>() {

    /** 현재 선택된 아이템 id */
    private var selectedId: String? = null

    /** 리스트 교체 (탭 변경 시 사용) */
    fun submitList(newItems: List<DecorateItem>) {
        items = newItems
        selectedId = null
        notifyDataSetChanged()
    }

    /** 선택된 아이템 반환 */
    fun getSelectedItem(): DecorateItem? {
        return items.firstOrNull { it.id == selectedId }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecorateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_decorate_grid, parent, false)
        return DecorateViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DecorateViewHolder, position: Int) {
        val item = items[position]

        // 썸네일 (Uri 우선, 없으면 drawable)
        when {
            item.imageUri != null -> holder.ivThumb.setImageURI(item.imageUri)
            item.imageRes != null -> holder.ivThumb.setImageResource(item.imageRes)
            else -> holder.ivThumb.setImageResource(R.drawable.splash_logo)
        }

        // 즐겨찾기 별 (지금은 항상 표시, 나중에 로직 분기 가능)
        holder.ivStar.visibility = View.VISIBLE
        // if (item.isFavorite) View.VISIBLE else View.GONE

        // 선택 테두리
        holder.viewSelectedBorder.visibility =
            if (item.id == selectedId) View.VISIBLE else View.GONE

        // 클릭 처리 (toggle 선택/해제)
        holder.itemView.setOnClickListener {
            selectedId = if (selectedId == item.id) null else item.id
            notifyDataSetChanged()
            onItemClick(item)
        }
    }

    class DecorateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivThumb)
        val ivStar: ImageView = itemView.findViewById(R.id.ivStar)
        val viewSelectedBorder: View = itemView.findViewById(R.id.viewSelectedBorder)
    }
}
