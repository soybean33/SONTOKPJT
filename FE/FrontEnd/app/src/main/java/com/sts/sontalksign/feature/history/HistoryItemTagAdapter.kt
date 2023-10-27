
package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryItemTagBinding
import com.sts.sontalksign.feature.common.CommonTagItem

class HistoryItemTagAdapter(private val historyItemList: ArrayList<CommonTagItem>) : RecyclerView.Adapter<HistoryItemTagAdapter.TagViewHolder>() {



    inner class TagViewHolder(private val binding: HistoryItemTagBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommonTagItem) {
            binding.tvHistoryTag.text = item.tagText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = HistoryItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = historyItemList.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(historyItemList[position])
    }
}
