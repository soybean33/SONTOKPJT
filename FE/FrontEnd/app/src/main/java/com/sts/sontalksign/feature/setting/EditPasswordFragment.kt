package com.sts.sontalksign.feature.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.FragmentEditPasswordBinding

class EditPasswordFragment : Fragment() {

    private lateinit var binding: FragmentEditPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_password, container, false)

        binding = FragmentEditPasswordBinding.bind(view)

        return view
    }

}