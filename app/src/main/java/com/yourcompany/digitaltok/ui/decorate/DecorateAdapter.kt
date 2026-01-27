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

    // 여러 개 즐겨찾기(별 체크) 허용
    private val pinnedIds = linkedSetOf<String>() // 순서 유지(Set)

    /** 리스트 교체 (탭 변경 시 사용) */
    fun submitList(newItems: List<DecorateItem>) {
        items = newItems
        selectedId = null

        val idSet = newItems.map { it.id }.toHashSet()
        pinnedIds.retainAll(idSet)

        notifyDataSetChanged()
    }

    /** 선택된 아이템 반환 */
    fun getSelectedItem(): DecorateItem? {
        return items.firstOrNull { it.id == selectedId }
    }

    // 별 토글 + 즐겨찾기 항목들을 위로 정렬
    private fun togglePinAndReorder(item: DecorateItem) {
        if (pinnedIds.contains(item.id)) pinnedIds.remove(item.id) else pinnedIds.add(item.id)

        // 즐겨찾기(true) 먼저, 그 다음 원래 순서 유지
        val reordered = items
            .withIndex()
            .sortedWith(compareByDescending<IndexedValue<DecorateItem>> { pinnedIds.contains(it.value.id) }
                .thenBy { it.index })
            .map { it.value }

        items = reordered
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecorateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_decorate_grid, parent, false)
        return DecorateViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DecorateViewHolder, position: Int) {
        val item = items[position]

        // 썸네일 (Uri 우선, 없으면 drawable, 둘 다 없으면 "무지 배경"만 보이게)
        when {
            item.imageUri != null -> {
                holder.ivThumb.visibility = View.VISIBLE
                holder.ivThumb.setImageURI(item.imageUri)
            }
            item.imageRes != null -> {
                holder.ivThumb.visibility = View.VISIBLE
                holder.ivThumb.setImageResource(item.imageRes)
            }
            else -> {
                holder.ivThumb.setImageDrawable(null)
                holder.ivThumb.visibility = View.GONE
            }
        }

        // 별 표시
        holder.ivStar.visibility = View.VISIBLE
        holder.ivStar.isSelected = pinnedIds.contains(item.id)
        holder.ivStar.bringToFront()

        // 선택 테두리
        holder.viewSelectedBorder.visibility =
            if (item.id == selectedId) View.VISIBLE else View.GONE

        // 클릭 처리 (toggle 선택/해제)
        holder.itemView.setOnClickListener {
            selectedId = if (selectedId == item.id) null else item.id
            notifyDataSetChanged()
            onItemClick(item)
        }

        // 별 클릭: 여러 개 체크 + 즐겨찾기들은 위로 모으기
        holder.ivStar.setOnClickListener {
            togglePinAndReorder(item)
        }
    }

    class DecorateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivThumb)
        val ivStar: ImageView = itemView.findViewById(R.id.ivStar)
        val viewSelectedBorder: View = itemView.findViewById(R.id.viewSelectedBorder)
    }
}
