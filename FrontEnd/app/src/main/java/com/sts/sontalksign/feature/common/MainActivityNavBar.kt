package com.sts.sontalksign.feature.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
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

    private var selectIconColor = 0
    private var selectBackColor = 0
    private var unSelectIconColor = 0
    private var unSelectBackColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        selectIconColor = ContextCompat.getColor(this, R.color.rectangle100)
        selectBackColor = ContextCompat.getColor(this, R.color.rectangle500)
        unSelectIconColor = ContextCompat.getColor(this, R.color.rectangle500)
        unSelectBackColor = ContextCompat.getColor(this, R.color.base)


        initViewPager()

        TabLayoutMediator(binding.tlMainTabLayout, binding.vpMainViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.baseline_history_24)
                    val color = ContextCompat.getColor(this, R.color.rectangle500)
                    tab.icon?.setTint(color)
                }

                1 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.baseline_sign_language_24)
                    val iconDrawable = tab?.icon
                    iconDrawable?.setTint(selectIconColor)
                    tab.view?.setBackgroundColor(selectBackColor)
                }

//                2 -> {
//                    tab.icon = ContextCompat.getDrawable(this, R.drawable.baseline_settings_24)
//                    val color = ContextCompat.getColor(this, R.color.rectangle500)
//                    tab.icon?.setTint(color)
//                }
            }
        }.attach()

        binding.tlMainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position
                when (position) {
                    0 -> {
                        val iconDrawable = ContextCompat.getDrawable(this@MainActivityNavBar, R.drawable.baseline_history_24)
                        iconDrawable?.setTint(selectIconColor)
                        tab.icon = iconDrawable
                        tab.view.setBackgroundColor(selectBackColor)
                    }

                    1 -> {
                        val iconDrawable = ContextCompat.getDrawable(this@MainActivityNavBar, R.drawable.baseline_sign_language_24)
                        iconDrawable?.setTint(selectIconColor)
                        tab.icon = iconDrawable
                        tab.view.setBackgroundColor(selectBackColor)
                    }

//                    2 -> {
//                        val iconDrawable = ContextCompat.getDrawable(this@MainActivityNavBar, R.drawable.baseline_settings_24)
//                        iconDrawable?.setTint(selectIconColor)
//                        tab.icon = iconDrawable
//                        tab.view.setBackgroundColor(selectBackColor)
//                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val iconDrawable = tab?.icon
                iconDrawable?.setTint(unSelectIconColor)
                tab?.view?.setBackgroundColor(unSelectBackColor)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun initViewPager() {
        var viewPager2Adatper = ViewPager2Adapter(this)
        viewPager2Adatper.addFragment(HistoryFragment())
        viewPager2Adatper.addFragment(ConversationFragment())
        // viewPager2Adatper.addFragment(SettingFragment())

        binding.vpMainViewPager.apply {
            adapter = viewPager2Adatper

            setCurrentItem(1, false)
        }
    }
}

