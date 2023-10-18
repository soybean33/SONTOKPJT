package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryItemBinding


class HistoryListAdapter (val historyList: ArrayList<HistoryListModel>) : RecyclerView.Adapter<HistoryListAdapter.CustomViewHolder>() {
//    private lateinit var historyItemTagAdapter: HistoryItemTagAdapter
//
//    var historyItemList: ArrayList<HistoryItemModel> = ArrayList()
    inner class CustomViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvHistoryTitle: TextView = binding.tvHistoryItemTitle
        val tvEndedTime: TextView = binding.tvHistoryItemTime
        val rvHistoryItemTagCompoenet: RecyclerView = binding.rvHistoryItemTagCompoenet

        fun bind(historyModel: HistoryListModel) {
            tvHistoryTitle.text = historyModel.historyTitle
            tvEndedTime.text = historyModel.EndedTime

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryListAdapter.CustomViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)



    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryListAdapter.CustomViewHolder, position: Int) {
        val historyList = historyList[position]
        holder.bind(historyList)

        // Get the RecyclerView of the inner item.
        val innerRecyclerView = holder.rvHistoryItemTagCompoenet

        // Create an adapter for the inner RecyclerView and bind data to it.
        val innerAdapter = HistoryItemTagAdapter(historyList.historyitemmodel)
        innerRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        innerRecyclerView.adapter = innerAdapter

    }




}