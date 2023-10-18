package com.sts.sontalksign.feature.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sts.sontalksign.databinding.HistoryItemTagBinding

class HistoryItemTagAdapter(private val context: Context, private val historyItemList: ArrayList<HistoryItemModel>) : BaseAdapter() {

    override fun getCount(): Int {
        return historyItemList.size
    }

    override fun getItem(position: Int): Any {
        return historyItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: HistoryItemTagBinding
        if (convertView == null) {
            binding = HistoryItemTagBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as HistoryItemTagBinding
        }

        val currentItem = historyItemList[position]
        binding.tvHistoryTagItemShape.text = currentItem.historyItemTag.toString()

        return binding.root
    }
}
