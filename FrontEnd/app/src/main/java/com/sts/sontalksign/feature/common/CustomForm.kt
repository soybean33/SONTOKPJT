package com.sts.sontalksign.feature.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sts.sontalksign.databinding.CustomFormBinding

class CustomForm(private val context : AppCompatActivity) {
    private lateinit var binding : CustomFormBinding
    private val fDialog = Dialog(context)
    private lateinit var onClickListener: onBtnStoreClickedListener

    private val tagAdapter: CommonTagAdapter by lazy {
        CommonTagAdapter(TagSingleton.tagList)
    }

    fun show() {
        binding = CustomFormBinding.inflate(context.layoutInflater)
        fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //fDialog.setCancelable(false)
        fDialog.setContentView(binding.root)
        fDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fDialog.show()

//        binding.lvTagList.visibility = View.GONE
        loadTagView()

        binding.btnStore.setOnClickListener {
            Log.d("CustomForm", "btnStore is clicked")
            onClickListener.onBtnStoreClicked(binding.ciTitleConversation.etInputContent.text.toString(), binding.ciTagConversation.etInputContent.text.toString())
            fDialog.dismiss()
        }

        binding.ciTagConversation.setOnClickListener {
            if(binding.lvTagList.visibility != View.VISIBLE) {
                binding.lvTagList.visibility = View.VISIBLE
            }
        }
    }

    fun loadTagView() {
        binding.lvTagList.adapter = tagAdapter
//        binding.lvTagList.setOnClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
//            val tag = parent.getItemAtPosition(position) as CommonTagItem
//            Toast.makeText(this, tag.tagText, Toast.LENGTH_LONG).show()
//        }

//        for(tag in TagSingleton.tagList) {
//
//        }
    }


    fun setOnBtnStoreClickedListener(listener: onBtnStoreClickedListener) {
        onClickListener = listener
    }

    interface onBtnStoreClickedListener {
        //TODO: tags: ArrayList<Int> 로 수정 필요
        fun onBtnStoreClicked(title: String, tags: String)
    }
}