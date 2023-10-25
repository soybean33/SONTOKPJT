package com.sts.sontalksign.feature.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sts.sontalksign.R

class CommonTagAdapter(private val tags: MutableList<CommonTagItem>) : BaseAdapter() {
    override fun getCount(): Int = tags.size

    override fun getItem(position: Int): CommonTagItem = tags[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        if(convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.history_tag_item, parent, false)

        val item: CommonTagItem = tags[position]
        val tagView = convertView!!.findViewById<TextView>(R.id.tv_tag)
//        tagView.background = ContextCompat.getDrawable(tagView.context, Color.parseColor(item.tagColor))
        tagView.background = ColorDrawable(Color.parseColor(item.tagColor))
        tagView.text = item.tagText

        return convertView
    }
}