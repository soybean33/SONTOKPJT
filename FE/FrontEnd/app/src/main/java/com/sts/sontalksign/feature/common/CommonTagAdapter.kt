package com.sts.sontalksign.feature.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.R
import org.w3c.dom.Text

/**
 * 대화내용 저장 FORM에서 사용하는 TAG 리스트의 Adapter
 * tags에는 현재 저장된 개인의 태그 정보가 저장되어 있습니다.
 */
class CommonTagAdapter(private val tags: List<CommonTagItem>) : RecyclerView.Adapter<CommonTagAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_tag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tags[position])

//        val layoutParams = holder.itemView.layoutParams as GridLayoutManager.LayoutParams
//        layoutParams.width = if (position % 2 == 0) {
//            2
//        } else {
//            1
//        }
//        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val tagView: CardView = itemView.findViewById(R.id.mcv_tag)
        private val tagContent: TextView = itemView.findViewById(R.id.tv_tag)

        fun bind(item: CommonTagItem) {
            //TODO: 색상의 개수 상수화
            tagView.setCardBackgroundColor(TagSingleton.colorList[item.tagInd.toInt() % 10]) //MaterialCardView의 배경색상 지정
            tagContent.text = item.tagText //TextView의 텍스트 지정
        }


    }
}