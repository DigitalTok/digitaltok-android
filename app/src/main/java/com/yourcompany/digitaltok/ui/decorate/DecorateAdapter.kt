package com.yourcompany.digitaltok.ui.decorate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.digitaltok.R

class DecorateAdapter(
    private var items: List<DecorateItem>,
    private val onItemClick: (DecorateItem) -> Unit,
    private val onFavoriteClick: (String, Boolean) -> Unit // (imageId, isFavorite)
) : RecyclerView.Adapter<DecorateAdapter.DecorateViewHolder>() {

    //현재 선택된 아이템 id
    private var selectedId: String? = null

    // 여러 개 즐겨찾기(별 체크) 허용
    private val pinnedIds = linkedSetOf<String>() // 순서 유지(Set)

    /**
     * 리스트 교체 (탭 변경 및 초기 데이터 로드 시 사용)
     * @param newItems 새로운 아이템 목록
     * @param newPinnedIds 즐겨찾기 상태인 아이템 ID 목록
     */
    fun submitList(newItems: List<DecorateItem>, newPinnedIds: Set<String>? = null) {
        items = newItems
        selectedId = null

        newPinnedIds?.let {
            pinnedIds.clear()
            pinnedIds.addAll(it)
        }

        // 즐겨찾기된 아이템들을 앞으로 정렬
        sortItems()
        notifyDataSetChanged()
    }

    /** 선택된 아이템 반환 */
    fun getSelectedItem(): DecorateItem? {
        return items.firstOrNull { it.id == selectedId }
    }

    // 별 토글 + 즐겨찾기 항목들을 위로 정렬
    private fun togglePinAndReorder(item: DecorateItem) {
        val isCurrentlyFavorite = pinnedIds.contains(item.id)
        val newFavoriteState = !isCurrentlyFavorite

        onFavoriteClick(item.id, newFavoriteState) // ViewModel에 알림

        if (newFavoriteState) {
            pinnedIds.add(item.id)
        } else {
            pinnedIds.remove(item.id)
        }

        sortItems()
        notifyDataSetChanged()
    }


     //즐겨찾기(pinned)된 아이템을 목록의 맨 위로, 나머지는 원래 순서대로 정렬
    private fun sortItems() {
        items = items
            .sortedWith(compareByDescending<DecorateItem> { pinnedIds.contains(it.id) })
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
