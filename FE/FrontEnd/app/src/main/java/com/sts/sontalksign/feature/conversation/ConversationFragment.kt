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
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ConversationFragment : Fragment() {
    private val TAG: String = "ConversationFragment"

    private val binding by lazy {
        FragmentConversationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //대화 시작 버튼 - 이벤틑 처리
        binding.btnStartConversation.setOnClickListener {
            Log.d(TAG, "btnGotoMain is clicked!!")
            val intent = Intent(this.activity, ConversationActivity::class.java)
            intent.putExtra("isRecord", binding.recordSwitch.isChecked)
            startActivity(intent)
        }

        return binding.root
    }
}