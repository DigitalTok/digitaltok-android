package com.example.digitaltok

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.digitaltok.databinding.ItemFaqBinding
import com.example.digitaltok.databinding.ItemFaqSupportBinding
import com.example.digitaltok.ui.theme.FaqItem

class FaqAdapter(
    private val items: List<FaqItem>,
    private val onSupportClick: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FAQ = 0
        private const val TYPE_SUPPORT = 1
    }

    // ✅ FAQ ViewHolder
    inner class FaqViewHolder(
        private val binding: ItemFaqBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaqItem) {
            binding.tvQuestion.text = item.question
            binding.tvAnswer.text = item.answer
        }
    }

    // ✅ CTA(footer) ViewHolder
    inner class SupportViewHolder(
        private val binding: ItemFaqSupportBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.btnContactSupport.setOnClickListener {
                onSupportClick?.invoke()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // 마지막 1개는 CTA footer
        return if (position == items.size) TYPE_SUPPORT else TYPE_FAQ
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_FAQ -> {
                val binding = ItemFaqBinding.inflate(inflater, parent, false)
                FaqViewHolder(binding)
            }
            else -> {
                val binding = ItemFaqSupportBinding.inflate(inflater, parent, false)
                SupportViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FaqViewHolder -> holder.bind(items[position])
            is SupportViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = items.size + 1 // ✅ 마지막 CTA 1개 추가
}
