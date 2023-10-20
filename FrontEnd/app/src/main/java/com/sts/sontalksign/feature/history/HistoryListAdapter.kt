
package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryItemBinding

class HistoryListAdapter(val historyList: ArrayList<HistoryListModel>) : RecyclerView.Adapter<HistoryListAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvHistoryTitle: TextView = binding.tvHistoryItemTitle
        val tvEndedTime: TextView = binding.tvHistoryItemTime
        val rvHistoryItemTagComponent: RecyclerView = binding.rvHistoryItemTagCompoenet

        fun bind(historyModel: HistoryListModel) {
            tvHistoryTitle.text = historyModel.historyTitle
            tvEndedTime.text = historyModel.EndedTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)

        val tagAdapter = HistoryItemTagAdapter(historyItem.historyitemmodel)
        holder.rvHistoryItemTagComponent.adapter = tagAdapter // 변경된 부분
        holder.rvHistoryItemTagComponent.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false) // 가로 스크롤을 위한 HORIZONTAL 설정
    }
}
