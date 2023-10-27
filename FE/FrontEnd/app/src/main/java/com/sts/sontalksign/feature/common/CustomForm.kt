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

class CustomForm(private val context : AppCompatActivity) {
    private lateinit var binding : CustomFormBinding
    private val fDialog = Dialog(context)
    private lateinit var onClickListener: onBtnStoreClickedListener

    private val TAG: String = "CustomForm"

    private val tagAdapter: CommonTagAdapter by lazy {
        CommonTagAdapter(TagSingleton.tagList)
    }

    fun show() {
        binding = CustomFormBinding.inflate(context.layoutInflater)
        fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fDialog.setContentView(binding.root)
        fDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        fDialog.show()

        loadTagView()

        /*버튼의 클릭 이벤트 처리*/
        //저장 버튼
        binding.btnStore.setOnClickListener {
            Log.d("CustomForm", "btnStore is clicked")
            onClickListener.onBtnStoreClicked(binding.ciTitleConversation.etInputContent.text.toString(), binding.ciTagConversation.etInputContent.text.toString())
            fDialog.dismiss()
        }

        //TODO: 타이틀 말고 입력칸 클릭시 이벤트 처리
        //태그 CustomInput
        binding.ciTagConversation.setOnClickListener {
            Log.d(TAG, "CustomInput is Clicked!!")
            if(binding.rvTagList.visibility == View.GONE) {
                binding.rvTagList.visibility = View.VISIBLE
            }
        }
    }

    fun loadTagView() {
        val layoutManager = GridLayoutManager(this.context, 4)
        binding.rvTagList.layoutManager = layoutManager
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            var sumLen : Int = 0
//            var rowPos : Int = 0
//            override fun getSpanSize(position: Int): Int {
//                var len : Int = TagSingleton.tagList[position].tagText.length
//                rowPos++
//                if(sumLen + len > 22) {
//                    sumLen = len
//                    rowPos = 1
//                    return rowPos
//                } else {
//                    sumLen += len
//                    return rowPos
//                }
//                // 여기에서 각 아이템의 열 수를 결정
//                // 예를 들어, position을 기반으로 열 수를 동적으로 설정할 수 있습니다.
//                //return if (position % 2 == 0) 1 else 2 // 예시: 홀수 위치는 2열, 짝수 위치는 1열
//            }
//        }

        binding.rvTagList.adapter = tagAdapter

        //TODO: TAG의 클릭 이벤트 처리
//        binding.lvTagList.setOnClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
//            val tag = parent.getItemAtPosition(position) as CommonTagItem
//            Toast.makeText(this, tag.tagText, Toast.LENGTH_LONG).show()
//        }
    }

    /**
     * 저장 버튼의 클릭 이벤트 리스터 지정
     */
    fun setOnBtnStoreClickedListener(listener: onBtnStoreClickedListener) {
        onClickListener = listener
    }

    interface onBtnStoreClickedListener {
        //TODO: tags: ArrayList<Int> 로 수정 필요
        fun onBtnStoreClicked(title: String, tags: String)
    }
}