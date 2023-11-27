package com.sts.sontalksign.feature.history

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.R

import com.sts.sontalksign.databinding.HistoryConversationBinding


class HistoryDetailConversationAdapter(private val historyDetailConList: ArrayList<HistoryDetailConversationModel>) :
    RecyclerView.Adapter<HistoryDetailConversationAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(private val binding: HistoryConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyDetailConversation: HistoryDetailConversationModel) {
            binding.tvHistoryConversationText.text = historyDetailConversation.historyText

            // 왼쪽/오른쪽 여부에 따라 메시지 아이템을 조절
            val isLeftMessage = historyDetailConversation.isLeft
            if (isLeftMessage) {
                binding.tvHistoryConversationText.setBackgroundResource(R.drawable.rounded_conversation_item_right)
                binding.tvHistoryConversationText.gravity = Gravity.RIGHT //오른쪽 정렬
                binding.spaceLeft.visibility = View.VISIBLE
                binding.spaceRight.visibility = View.GONE
                binding.llhHistoryConversationText.gravity = Gravity.RIGHT
            } else {
                binding.tvHistoryConversationText.setBackgroundResource(R.drawable.rounded_conversation_item_left)
                binding.tvHistoryConversationText.gravity = Gravity.LEFT //왼쪽 정렬
                binding.spaceRight.visibility = View.VISIBLE
                binding.spaceLeft.visibility = View.GONE
                binding.llhHistoryConversationText.gravity = Gravity.LEFT
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = HistoryConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return historyDetailConList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(historyDetailConList[position])
    }
}

