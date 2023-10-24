package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.ActivityConversationBinding
import com.sts.sontalksign.databinding.HistoryDetailConversationBinding

abstract class HistoryDetailConversationAdapter (val historyConversationList: ArrayList<HistoryDetailConversationModel>) : RecyclerView.Adapter<HistoryDetailConversationAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(private val binding: HistoryDetailConversationBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(historyDetailConversation: HistoryDetailConversationModel) {

        }

    }

    fun bind(historyConversationListModel: HistoryDetailConversationModel) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = HistoryDetailConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return historyConversationList.size
    }


}

