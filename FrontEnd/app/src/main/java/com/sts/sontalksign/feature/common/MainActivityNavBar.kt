package com.sts.sontalksign.feature.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityMainNavBarBinding
import com.sts.sontalksign.feature.conversation.ConversationFragment
import com.sts.sontalksign.feature.history.HistoryFragment
import com.sts.sontalksign.feature.setting.SettingFragment

class MainActivityNavBar : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainNavBarBinding.inflate(layoutInflater)
    }

    private val fragmentState: ArrayList<Boolean> = arrayListOf(false, true, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViewPager()
    }

    private fun initViewPager() {
        var viewPager2Adatper = ViewPager2Adapter(this)
        viewPager2Adatper.addFragment(HistoryFragment())
        viewPager2Adatper.addFragment(ConversationFragment())
        viewPager2Adatper.addFragment(SettingFragment())

        binding.vpMainViewPager.apply {
            adapter = viewPager2Adatper

            setCurrentItem(1, false)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    when (position) {
                        0 -> {
                            setFrag(0)
                            changeNavColor()
                        }
                        1 -> {
                            setFrag(1)
                            changeNavColor()
                        }
                        2 -> {
                            setFrag(2)
                            changeNavColor()
                        }
                    }
                }
            })
        }

    }

    private fun changeNavColor() {
        if(fragmentState[0]) {
            binding.ivGotoHistory.setColorFilter(getColor(R.color.rectangle100))
            binding.flGotoHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.rectangle500))

            binding.ivGotoConversation.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoSetting.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.base))
        }
        else if(fragmentState[1]) {
            binding.ivGotoHistory.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoConversation.setColorFilter(getColor(R.color.rectangle100))
            binding.flGotoConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.rectangle500))

            binding.ivGotoSetting.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.base))
        }
        else if(fragmentState[2]) {
            binding.ivGotoHistory.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoConversation.setColorFilter(getColor(R.color.rectangle500))
            binding.flGotoConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.base))

            binding.ivGotoSetting.setColorFilter(getColor(R.color.rectangle100))
            binding.flGotoSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.rectangle500))
        }
    }

    private fun setFrag(fragNum: Int) {
        when(fragNum){
            0 -> {
                fragmentState[0] = true
                fragmentState[1] = false
                fragmentState[2] = false
            }
            1 -> {
                fragmentState[0] = false
                fragmentState[1] = true
                fragmentState[2] = false
            }
            2 -> {
                fragmentState[0] = false
                fragmentState[1] = false
                fragmentState[2] = true
            }
        }
    }
}