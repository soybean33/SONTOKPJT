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
import androidx.recyclerview.widget.GridLayoutManager
import com.sts.sontalksign.databinding.CustomFormBinding
import com.sts.sontalksign.databinding.CustomNoRecordFormBinding

class CustomNoRecordForm(private val context : AppCompatActivity) {
    private lateinit var binding: CustomNoRecordFormBinding
    private val fDialog = Dia log(context)
    private lateinit var onClickListener: CustomForm.onBtnStoreClickedListener



    fun show() {
        binding = CustomNoRecordFormBinding.inflate(context.layoutInflater)
        fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fDialog.setContentView(binding.root)
        fDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        fDialog.show()



        /*버튼의 클릭 이벤트 처리*/
        //저장 버튼
        binding.btnStore.setOnClickListener {
            Log.d("CustomForm", "btnStore is clicked")
            onClickListener.onBtnStoreClicked(

            )
            fDialog.dismiss()
        }

        //TODO: 타이틀 말고 입력칸 클릭시 이벤트 처리
        //태그 CustomInput
        binding.ciTagConversation.setOnClickListener {
            Log.d(TAG, "CustomInput is Clicked!!")
            if (binding.rvTagList.visibility == View.GONE) {
                binding.rvTagList.visibility = View.VISIBLE
            }
        }
    }

}