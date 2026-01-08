package com.example.digitaltok

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LastItemBottomSpaceDecoration(private val bottomPx: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val lastIndex = (parent.adapter?.itemCount ?: 0) - 1
        // 마지막 아이템(=footer) "바로 위 아이템"에 140을 주고 싶으면 아래처럼:
        if (position == lastIndex - 1) {
            outRect.bottom = bottomPx
        }
    }
}