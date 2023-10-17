package com.sts.sontalksign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.sts.sontalksign.databinding.ActivityMainNavBarBinding

class MainActivityNavBar : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainNavBarBinding.inflate(layoutInflater)
    }

    private val fragmentState: ArrayList<Boolean> = arrayListOf(false, true, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFrag(1)
        changeNavColor()

        binding.ivGotoHistory.setOnClickListener {
            setFrag(0)
        }
    }

    private fun changeNavColor() {
        if(fragmentState[0]) {
            binding.ivGotoHistory.setColorFilter(getColor(R.color.rectangle100))
            binding.flGotoHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.rectangle500))

            binding.ivGotoConversation.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoSetting.setColorFilter(getColor(R.color.rectangle500))
            binding.ivGotoSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.base))
        }
        else if(fragmentState[1]) {
            binding.ivGotoHistory.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoConversation.setColorFilter(getColor(R.color.rectangle100))
            binding.flGotoConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.rectangle500))

            binding.ivGotoSetting.setColorFilter(getColor(R.color.rectangle500))
            binding.ivGotoSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.base))
        }
        else if(fragmentState[2]) {
            binding.ivGotoHistory.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoConversation.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoSetting.setColorFilter(getColor(R.color.rectangle100))
            binding.ivGotoSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.rectangle500))
        }
    }

    private fun setFrag(fragNum: Int) {
        val ft = supportFragmentManager.beginTransaction()

        when(fragNum){
            0 -> {
                fragmentState[0] = true
                fragmentState[1] = false
                fragmentState[2] = false

                val fragment = HistoryFragment()
                ft.replace(R.id.fl_MainFragment, fragment).commit()
            }
            1 -> {
                fragmentState[0] = false
                fragmentState[1] = true
                fragmentState[2] = false

                val fragment = ConversationFragment()
                ft.replace(R.id.fl_MainFragment, fragment).commit()
            }
            2 -> {
                fragmentState[0] = false
                fragmentState[1] = false
                fragmentState[2] = true

                val fragment = SettingFragment()
                ft.replace(R.id.fl_MainFragment, fragment).commit()
            }
        }
    }
}