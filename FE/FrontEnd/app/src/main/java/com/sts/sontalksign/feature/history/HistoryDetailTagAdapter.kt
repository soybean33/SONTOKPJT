package com.sts.sontalksign.feature.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityHistoryDetailBinding
import com.sts.sontalksign.databinding.HistoryDetailTagBinding
import com.sts.sontalksign.feature.common.CommonTagAdapter
import com.sts.sontalksign.feature.common.CommonTagItem
import com.sts.sontalksign.feature.common.TagSingleton

class HistoryDetailTagAdapter(private val tags: List<CommonTagItem>) : RecyclerView.Adapter<HistoryDetailTagAdapter.DetailTagViewHolder>() {

    inner class DetailTagViewHolder(view : View) : RecyclerView.ViewHolder(view){


        private val tagView: CardView = itemView.findViewById(R.id.mcv_tag)
        private val tagContent: TextView = itemView.findViewById(R.id.tv_tag)
        fun bind(item: CommonTagItem) {
            tagView.setCardBackgroundColor(TagSingleton.colorList[item.tagInd.toInt() % 10]) //MaterialCardView의 배경색상 지정
            tagContent.text = item.tagText //TextView의 텍스트 지정
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_tag_item, parent, false)
        return DetailTagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: DetailTagViewHolder, position: Int) {
        holder.bind(tags[position])
    }
}

