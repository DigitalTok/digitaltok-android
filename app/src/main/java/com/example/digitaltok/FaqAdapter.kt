package com.example.digitaltok
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.digitaltok.databinding.ItemFaqBinding
import com.example.digitaltok.ui.theme.FaqItem

class FaqAdapter(
    private val items: List<FaqItem>
) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    inner class FaqViewHolder(
        private val binding: ItemFaqBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaqItem) {
            // item_faq.xml에서 Q/A 라벨은 고정이고,
            // 실제로 바뀌는 텍스트만 데이터로 채움
            binding.tvQuestion.text = item.question
            binding.tvAnswer.text = item.answer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
