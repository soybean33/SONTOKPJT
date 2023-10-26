package com.sts.sontalksign.feature.conversation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sts.sontalksign.databinding.FragmentConversationBinding
import java.io.File

class ConversationFragment : Fragment() {

    private final val TAG: String = "ConversationFragment"

    private val binding by lazy {
        FragmentConversationBinding.inflate(layoutInflater)
    }

    private var directory: String? = null
    private var filename: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //시작 화면에 최초 접근 - 사용자의 커스텀 tag 파일이 없다면 생성 (TAGS.txt)
        directory = requireActivity().filesDir.absolutePath //내부경로의 절대 경로
        filename = "TAGS"

        checkTagFile()

        //대화 시작 버튼 - 이벤틑 처리
        binding.btnStartConversation.setOnClickListener {
            Log.d(TAG, "btnGotoMain is clicked!!")
            val intent = Intent(this.activity, ConversationActivity::class.java)
            intent.putExtra("isRecord", binding.recordSwitch.isChecked)
            startActivity(intent)
        }

        return binding.root
    }

    fun checkTagFile() {
        //파일 미존재 시 디렉토리 및 파일 생성
        val dir = File(directory)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }
}