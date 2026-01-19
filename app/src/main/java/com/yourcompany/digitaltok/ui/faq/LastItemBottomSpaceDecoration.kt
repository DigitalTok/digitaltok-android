package com.yourcompany.digitaltok.ui.faq

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LastItemBottomSpaceDecoration(
    private val bottomPx: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val lastIndex = (parent.adapter?.itemCount ?: return) - 1

        // footer(마지막 아이템)에만 bottom space
        outRect.bottom = if (position == lastIndex) bottomPx else 0

    }
}