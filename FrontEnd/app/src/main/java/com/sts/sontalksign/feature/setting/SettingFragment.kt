package com.sts.sontalksign.feature.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        binding = FragmentSettingBinding.bind(view)

//        binding.llhEditPassword.setOnClickListener {
//            val ft = requireActivity().supportFragmentManager.beginTransaction()
//            val changeFragment = EditPasswordFragment()
//            ft.replace(R.id.fl_MainFragment, changeFragment).commit()
//        }

        return view
    }

}