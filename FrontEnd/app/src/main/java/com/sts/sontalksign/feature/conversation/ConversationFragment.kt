package com.sts.sontalksign.feature.conversation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sts.sontalksign.databinding.FragmentConversationBinding

class ConversationFragment : Fragment() {

    private final val TAG: String = "ConversationFragment"

    private val binding by lazy {
        FragmentConversationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.btnStartConversation.setOnClickListener {
            Log.d(TAG, "btnGotoMain is clicked!!")
            val intent = Intent(this.activity, ConversationActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}