package com.sts.sontalksign.feature.conversation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sts.sontalksign.databinding.ActivityConversationBinding

class ConversationActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    private val FLAG_PERM_CAMERA = 98
    private val FLAG_REQ_CAMERA = 101

    private val binding by lazy {
        ActivityConversationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_conversation)
        setContentView(binding.root)

        openCamera() //카메라 호출
    }

    private fun openCamera() {
        //카메라 권한 확인
        if(checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)) {
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //startActivityForResult()
        }
    }

    //권한 체크 - 카메라
    private fun checkPermission(permissions:Array<out String>, flag:Int):Boolean {
        //AOS 버전이 Tiramisu(API 33) 이상일 때만 - 현재 z flip3 지원 버전(23.10.17 기준)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for(permission in permissions) {
                //권한 미승인 항목은 권한 승인 요청을 사용자 화면에 호출
                if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }

        return true
    }
}