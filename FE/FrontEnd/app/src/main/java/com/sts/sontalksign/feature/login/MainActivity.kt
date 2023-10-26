package com.sts.sontalksign.feature.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sts.sontalksign.feature.common.MainActivityNavBar
import com.sts.sontalksign.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivityNavBar::class.java)  // [이동할 엑티비티 이름]::class.java
            startActivity(intent)
            finish()
        }
    }
}