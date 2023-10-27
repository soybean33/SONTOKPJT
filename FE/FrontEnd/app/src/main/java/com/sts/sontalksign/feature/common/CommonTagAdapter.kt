package com.sts.sontalksign.feature.common

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

/**
 * 대화내용 저장 FORM에서 사용하는 TAG 리스트의 Adapter
 * tags에는 현재 저장된 개인의 태그 정보가 저장되어 있습니다.
 */
class CommonTagAdapter(private val tags: List<CommonTagItem>) : RecyclerView.Adapter<CommonTagAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagView: CardView = itemView.findViewById(R.id.mcv_tag)
        val tagContent: TextView = itemView.findViewById(R.id.tv_tag)
    }

    override fun getItemCount(): Int = tags.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // MaterialCardView으로 지정한 TAG
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_tag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommonTagAdapter.ViewHolder, position: Int) {
        Log.d("CommonTagAdapter", position.toString())
        val item = tags[position]
        //TODO: 색상의 개수 상수화
        holder.tagView.setCardBackgroundColor((TagSingleton.colorList[position % 10])) // MaterialCardView의 배경색상 지정
        holder.tagContent.text = item.tagText // TextView의 텍스트 지정

        val layoutParams = holder.tagView.layoutParams as GridLayoutManager.LayoutParams
        layoutParams.width = if (position % 2 == 0) {
            2
        } else {
            1
        }
        holder.tagView.layoutParams = layoutParams
    }
}