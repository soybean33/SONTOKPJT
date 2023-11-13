package com.sts.sontalksign.feature.common

import android.app.Dialog
import android.content.Intent
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
import com.sts.sontalksign.feature.conversation.ConversationActivity
import com.sts.sontalksign.feature.conversation.ConversationFragment
import com.sts.sontalksign.feature.history.HistoryDetailActivity

class CustomNoRecordForm(private val context : AppCompatActivity) {
    private lateinit var binding: CustomNoRecordFormBinding
    private val fDialog = Dialog(context)




    fun show() {
        binding = CustomNoRecordFormBinding.inflate(context.layoutInflater)
        fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fDialog.setContentView(binding.root)
        fDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        fDialog.show()



        /*버튼의 클릭 이벤트 처리*/
        //저장 버튼
        binding.btnDismiss.setOnClickListener {
            Log.d("btnDismiss", "btnDismiss is clicked")
            val intent = Intent(binding.root.context, ConversationFragment::class.java)

            binding.root.context.startActivity(intent)
            fDialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {

            fDialog.dismiss()
        }

        //TODO: 타이틀 말고 입력칸 클릭시 이벤트 처리
        //태그 CustomInput

    }

}