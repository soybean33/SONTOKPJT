
package com.sts.sontalksign.feature.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.HistoryItemBinding

class HistoryListAdapter(val historyList: ArrayList<HistoryListModel>) : RecyclerView.Adapter<HistoryListAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(private val binding: HistoryItemBinding) :

        RecyclerView.ViewHolder(binding.root) {

        val tvHistoryTitle: TextView = binding.tvHistoryItemTitle
        val tvEndedTime: TextView = binding.tvHistoryItemTime
        val rvHistoryItemTagComponent: RecyclerView = binding.rvHistoryItemTagCompoenet

        fun bind(historyModel: HistoryListModel) {
            tvHistoryTitle.text = historyModel.historyTitle
            tvEndedTime.text = historyModel.EndedTime.toString() // 형변환을 고려해야 할 수도 있습니다.


//            // 리소스 이름을 사용하여 리소스 ID를 가져옵니다.
//            val imageResourceID = itemView.resources.getIdentifier(
//                resourceName,
//                "drawable",
//                itemView.context.packageName
//            )

            binding.llvHistoryItem.setOnClickListener{
                val curPos: Int = adapterPosition
                val HistoryList: HistoryListModel = historyList[curPos] // myassetItemList로 수정

                val intent = Intent(binding.root.context,HistoryDetailActivity::class.java)
                intent.putExtra("historyTitle", HistoryList.historyTitle)
                binding.root.context.startActivity(intent)

            }


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
