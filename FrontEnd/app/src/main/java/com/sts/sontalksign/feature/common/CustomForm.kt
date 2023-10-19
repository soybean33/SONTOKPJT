package com.sts.sontalksign.feature.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.sts.sontalksign.databinding.CustomFormBinding

class CustomForm(private val context : AppCompatActivity) {
    private lateinit var binding : CustomFormBinding
    private val fDialog = Dialog(context)
    private lateinit var listener: CustomFormBtnStoreClickedListener

    fun show(content : String) {
        binding = CustomFormBinding.inflate(context.layoutInflater)

        fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fDialog.setContentView(binding.root)
        fDialog.setCancelable(false)

        binding.btnStore.setOnClickListener {
            //저장
           listener.onBtnStoreClicked("저장되었습니다")
            fDialog.dismiss()
        }

        fDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fDialog.show()
    }

    fun setOnBtnStoreClickedListener(listener: (String) -> Unit) {
        this.listener =object: CustomFormBtnStoreClickedListener {
            override fun onBtnStoreClicked(content: String) {
                listener(content)
            }
        }
    }

    interface CustomFormBtnStoreClickedListener {
        fun onBtnStoreClicked(content: String)
    }
}