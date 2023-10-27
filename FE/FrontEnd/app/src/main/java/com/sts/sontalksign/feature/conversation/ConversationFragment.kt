package com.sts.sontalksign.feature.conversation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.FragmentConversationBinding
import com.sts.sontalksign.feature.common.CommonTagItem
import com.sts.sontalksign.feature.common.TagSingleton
import com.sts.sontalksign.global.FileFormats
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class ConversationFragment : Fragment() {

    private val TAG: String = "ConversationFragment"

    private val binding by lazy {
        FragmentConversationBinding.inflate(layoutInflater)
    }

    private var directory: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //시작 화면에 최초 접근 - 사용자의 커스텀 tag 파일이 없다면 생성 (TAGS.txt)
        directory = requireActivity().filesDir.absolutePath //내부경로의 절대 경로

        getTagFileAndStore()

        //대화 시작 버튼 - 이벤틑 처리
        binding.btnStartConversation.setOnClickListener {
            Log.d(TAG, "btnGotoMain is clicked!!")
            val intent = Intent(this.activity, ConversationActivity::class.java)
            intent.putExtra("isRecord", binding.recordSwitch.isChecked)
            startActivity(intent)
        }

        return binding.root
    }

    fun getTagFileAndStore() {
        //개인의 TAG 정보 읽어와서 저장
        //파일 미존재 시 디렉토리 및 파일 생성
        val dir = File(directory)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val fullPath = directory + "/" + FileFormats.tagFileName
        val reader = FileReader(fullPath)
        val buffer = BufferedReader(reader)

        var line: String? = ""
        while(true) {
            line = buffer.readLine() //줄 단위로 read
            if(line == null) break
            else {
                val (index, text) = line.split(" ")
                TagSingleton.tagList.add(CommonTagItem(index, text))
            }
        }
        buffer.close()

        //시스템 TAG 색상 지정
        val cList = resources.getIntArray(R.array.tagColorArr)
        for(color in cList) {
            TagSingleton.colorList.add((color))
        }
    }
}