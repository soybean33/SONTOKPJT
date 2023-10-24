package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.ActivityHistoryDetailBinding
import com.sts.sontalksign.databinding.HistoryDetailTagBinding

class HistoryDetailTagAdapter(private val historyDetailTagList: ArrayList<HistoryDetailTagModel>) : RecyclerView.Adapter<HistoryDetailTagAdapter.DetailTagViewHolder>() {

    inner class DetailTagViewHolder(private val binding: HistoryDetailTagBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(detailTag: HistoryDetailTagModel) {
            binding.tvHistoryTagItemShape.text = detailTag.historyItemTag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTagViewHolder {
        val binding = HistoryDetailTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailTagViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historyDetailTagList.size
    }

    override fun onBindViewHolder(holder: DetailTagViewHolder, position: Int) {
        holder.bind(historyDetailTagList[position])
    }
}

