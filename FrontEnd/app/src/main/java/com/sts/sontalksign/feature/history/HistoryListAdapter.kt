package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryItemBinding

class HistoryListAdapter(val historyList: ArrayList<HistoryListModel>) : RecyclerView.Adapter<HistoryListAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvHistoryTitle: TextView = binding.tvHistoryItemTitle
        val tvEndedTime: TextView = binding.tvHistoryItemTime
        val lvHistoryItemTagComponent: ListView = binding.lvHistoryItemTagCompoenet

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
        val currentHistoryItem = historyList[position]   // <-- 변경된 변수명 사용
        holder.bind(currentHistoryItem)

        // Set up the inner ListView.
        val innerListView = holder.lvHistoryItemTagComponent

        // Create an adapter for the inner ListView and bind data to it.
        val innerAdapter = HistoryItemTagAdapter(holder.itemView.context, currentHistoryItem.historyitemmodel)
        innerListView.adapter = innerAdapter
    }
}
