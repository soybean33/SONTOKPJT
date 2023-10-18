package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryItemTagBinding

class HistoryItemTagAdaper (val historyItemList: ArrayList<HistoryItemModel>) : RecyclerView.Adapter<HistoryItemTagAdaper.CustomViewHolder>() {

    inner class CustomViewHolder(private val binding: HistoryItemTagBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvHistoryTagItemShape: TextView = binding.tvHistoryTagItemShape

        fun bind(historyItemModel: HistoryItemModel) {
            tvHistoryTagItemShape.text = historyItemModel.historyItemTag.toString()

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryItemTagAdaper.CustomViewHolder {
        val binding = HistoryItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return historyItemList.size
    }

    override fun onBindViewHolder(holder: HistoryItemTagAdaper.CustomViewHolder, position: Int) {
        val historyItemList = historyItemList[position]
        holder.bind(historyItemList)
    }
}