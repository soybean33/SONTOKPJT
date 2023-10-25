package com.sts.sontalksign.feature.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.sts.sontalksign.databinding.CustomFormBinding

class CustomForm(private val context : AppCompatActivity) {
    private lateinit var binding : CustomFormBinding
    private val fDialog = Dialog(context)
    private lateinit var onClickListener: onBtnStoreClickedListener

    fun show() {
        binding = CustomFormBinding.inflate(context.layoutInflater)
        fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fDialog.setCancelable(false)
        fDialog.setContentView(binding.root)
        fDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fDialog.show()

        binding.btnStore.setOnClickListener {
            Log.d("CustomForm", "btnStore is clicked")
            onClickListener.onBtnStoreClicked(binding.ciTitleConversation.etInputContent.text.toString(), binding.ciTagConversation.etInputContent.text.toString())
            fDialog.dismiss()
        }

    }

    fun setOnBtnStoreClickedListener(listener: onBtnStoreClickedListener) {
        onClickListener = listener
    }

    interface onBtnStoreClickedListener {
        //TODO: tags: ArrayList<Int> 로 수정 필요
        fun onBtnStoreClicked(title: String, tags: String)
    }
}